package com.application.echo.features.messaging.repository

import com.application.echo.api.message.MessageApiRepository
import com.application.echo.api.message.MessageResponse
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.core.common.repository.bufferedMutableSharedFlow
import com.application.echo.core.network.result.getOrNull
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.messaging.db.dao.ConversationDao
import com.application.echo.features.messaging.db.dao.MessageDao
import com.application.echo.features.messaging.db.dao.ParticipantDao
import com.application.echo.features.messaging.model.Conversation
import com.application.echo.features.messaging.model.Message
import com.application.echo.features.messaging.model.MessagingEvent
import com.application.echo.features.messaging.model.toDomain
import com.application.echo.features.messaging.model.toEntity
import com.application.echo.features.messaging.model.toParticipantEntities
import com.application.echo.features.messaging.outbox.MessageOutbox
import com.application.echo.features.messaging.store.TransientMessagingState
import com.application.echo.features.messaging.sync.SyncEngine
import com.application.echo.features.websocket.EchoWebSocketManager
import com.application.echo.features.websocket.model.SyncConversation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MessagingRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val participantDao: ParticipantDao,
    private val messageDao: MessageDao,
    private val messageApiRepository: MessageApiRepository,
    private val webSocketManager: EchoWebSocketManager,
    private val authRepository: AuthRepository,
    private val transientState: TransientMessagingState,
    private val outbox: MessageOutbox,
    private val syncEngine: SyncEngine,
    @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
) : MessagingRepository {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val _events = bufferedMutableSharedFlow<MessagingEvent>()

    private val currentUserId: String
        get() = authRepository.userStateFlow.value.userId

    init {
        observeAuthState()
        startOutboxProcessing()
    }

    // ── Reactive Queries ────────────────────────────────────────────────

    override val conversations: Flow<List<Conversation>> =
        conversationDao.observeAllWithParticipants().map { list ->
            list.map { it.toDomain() }
        }

    override fun messages(conversationId: String): Flow<List<Message>> =
        messageDao.observeByConversation(conversationId).map { list ->
            list.map { it.toDomain() }
        }

    override fun typingUsers(conversationId: String): Flow<Set<String>> =
        transientState.typing.map { it[conversationId].orEmpty() }

    override val totalUnreadCount: Flow<Int> =
        conversationDao.observeTotalUnreadCount()

    override fun unreadCount(conversationId: String): Flow<Int> =
        conversationDao.observeUnreadCount(conversationId)

    override val activeConversationId: StateFlow<String?> =
        transientState.activeConversationId

    override val events: Flow<MessagingEvent> = merge(_events, outbox.events)

    // ── Commands ────────────────────────────────────────────────────────

    override suspend fun refreshConversations() {
        syncEngine.syncConversations()
    }

    override suspend fun refreshMessages(conversationId: String) {
        syncEngine.syncMessages(conversationId)
    }

    override suspend fun loadOlderMessages(conversationId: String): Boolean {
        return syncEngine.loadOlderMessages(conversationId)
    }

    override suspend fun retryMessage(localId: String): Boolean {
        return outbox.retry(localId = localId, currentUserId = currentUserId)
    }

    override suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: String,
    ): Message {
        return outbox.enqueue(
            conversationId = conversationId,
            content = content,
            messageType = messageType,
            currentUserId = currentUserId,
        )
    }

    override suspend fun markAsRead(conversationId: String, messageIds: List<String>) {
        if (messageIds.isEmpty()) return

        // Update local DB immediately (optimistic)
        messageDao.markAsRead(conversationId, messageIds)
        conversationDao.setUnreadCount(conversationId, 0)

        // Mark the latest message as read via REST — the backend marks all messages
        // up to and including this one as read in the conversation.
        val latestMessageId = messageIds.last()
        messageApiRepository.markAsRead(latestMessageId)

        // Also notify via WebSocket for real-time broadcast to other participants
        webSocketManager.markRead(conversationId, messageIds)
        Timber.d("Marked %d messages as read in %s", messageIds.size, conversationId)
    }

    override suspend fun createConversation(
        conversationType: String,
        participantIds: List<String>,
    ): Conversation? {
        val response = messageApiRepository.createConversation(conversationType, participantIds)
            .getOrNull() ?: return null

        val entity = response.toEntity()
        val participants = response.toParticipantEntities()
        conversationDao.upsert(entity)
        participantDao.upsertAll(participants)

        _events.tryEmit(MessagingEvent.ConversationCreated(response.id))

        val withParticipants = com.application.echo.features.messaging.db.relation.ConversationWithParticipants(
            conversation = entity,
            participants = participants,
        )
        return withParticipants.toDomain()
    }

    override fun setActiveConversation(conversationId: String?) {
        transientState.setActiveConversation(conversationId)
        if (conversationId != null) {
            scope.launch {
                conversationDao.setUnreadCount(conversationId, 0)
            }
        }
    }

    // ── WebSocket Event Handlers ────────────────────────────────────────

    override suspend fun onNewMessageReceived(message: MessageResponse) {
        val entity = message.toEntity(currentUserId)
        messageDao.upsert(entity)

        val isIncomingMessage = message.senderId != currentUserId
        if (isIncomingMessage) {
            webSocketManager.markDelivered(message.conversationId, listOf(message.id))
        }

        conversationDao.updateLastMessage(
            conversationId = message.conversationId,
            msgId = message.id,
            content = message.content,
            senderId = message.senderId,
            timestamp = message.createdAt.orEmpty(),
            status = message.status,
        )

        val isConversationActive = transientState.activeConversationId.value == message.conversationId
        if (!isConversationActive) {
            conversationDao.incrementUnreadCount(message.conversationId)
        } else if (isIncomingMessage) {
            messageDao.markAsRead(message.conversationId, listOf(message.id))
            conversationDao.setUnreadCount(message.conversationId, 0)
            webSocketManager.markRead(message.conversationId, listOf(message.id))
        }

        _events.tryEmit(
            MessagingEvent.NewMessageReceived(
                conversationId = message.conversationId,
                messageId = message.id,
            )
        )

        Timber.d("New message received: %s in conversation %s", message.id, message.conversationId)
    }

    override suspend fun onMessagesRead(conversationId: String, messageIds: List<String>) {
        messageDao.markAsRead(conversationId, messageIds)
        Timber.d("Messages read by other user: %d in %s", messageIds.size, conversationId)
    }

    override fun onTypingEvent(conversationId: String, userId: String, isTyping: Boolean) {
        transientState.setTyping(conversationId, userId, isTyping)
    }

    override fun sendTyping(conversationId: String, isTyping: Boolean) {
        if (conversationId.isBlank()) return
        if (isTyping) {
            webSocketManager.sendTypingStart(conversationId)
        } else {
            webSocketManager.sendTypingStop(conversationId)
        }
    }

    override suspend fun onPresenceChanged(userId: String, status: String) {
        participantDao.updatePresence(userId, status)
        Timber.d("Presence changed: user=%s, status=%s", userId, status)
    }

    override suspend fun onSyncRequired(conversations: List<SyncConversation>) {
        Timber.d("Sync required for %d conversations", conversations.size)
        syncEngine.syncTargeted(conversations)
    }

    // ── Lifecycle ───────────────────────────────────────────────────────

    override suspend fun clear() {
        Timber.d("Clearing all messaging data")
        messageDao.deleteAll()
        conversationDao.deleteAll()
        participantDao.deleteAll()
        outbox.clear()
        transientState.clear()
        syncEngine.reset()
    }

    // ── Internal ────────────────────────────────────────────────────────

    private fun observeAuthState() {
        scope.launch {
            authRepository.authStateFlow.collect { state ->
                if (state is AuthState.Unauthenticated) {
                    clear()
                }
            }
        }
    }

    private fun startOutboxProcessing() {
        scope.launch {
            outbox.processQueue(currentUserId)
        }
    }
}
