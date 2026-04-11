package com.application.echo.features.messaging.outbox

import com.application.echo.api.message.MessageApiRepository
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.core.common.repository.bufferedMutableSharedFlow
import com.application.echo.core.network.result.fold
import com.application.echo.features.messaging.db.dao.ConversationDao
import com.application.echo.features.messaging.db.dao.MessageDao
import com.application.echo.features.messaging.db.dao.PendingMessageDao
import com.application.echo.features.messaging.db.entity.MessageEntity
import com.application.echo.features.messaging.db.entity.PendingMessageEntity
import com.application.echo.features.messaging.model.Message
import com.application.echo.features.messaging.model.MessageDeliveryStatus
import com.application.echo.features.messaging.model.MessagingEvent
import com.application.echo.features.messaging.model.toDomain
import com.application.echo.features.messaging.model.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class MessageOutboxImpl @Inject constructor(
    private val pendingMessageDao: PendingMessageDao,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val messageApiRepository: MessageApiRepository,
    @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
) : MessageOutbox {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val _events = bufferedMutableSharedFlow<MessagingEvent>()
    override val events: Flow<MessagingEvent> = _events

    override suspend fun enqueue(
        conversationId: String,
        content: String,
        messageType: String,
        currentUserId: String,
    ): Message {
        val localId = UUID.randomUUID().toString()
        val now = Clock.System.now().toString()

        // Insert into outbox (survives process death)
        val pendingEntity = PendingMessageEntity(
            localId = localId,
            conversationId = conversationId,
            content = content,
            messageType = messageType,
            createdAt = now,
        )
        pendingMessageDao.insert(pendingEntity)

        // Optimistic insert into messages table
        val optimisticEntity = MessageEntity(
            id = localId,
            conversationId = conversationId,
            senderId = currentUserId,
            content = content,
            messageType = messageType,
            status = MessageDeliveryStatus.PENDING.toRaw(),
            createdAt = now,
            updatedAt = now,
            isOutgoing = true,
            localId = localId,
        )
        messageDao.upsert(optimisticEntity)

        // Update conversation's last message
        conversationDao.updateLastMessage(
            conversationId = conversationId,
            msgId = localId,
            content = content,
            senderId = currentUserId,
            timestamp = now,
            status = MessageDeliveryStatus.PENDING.toRaw(),
        )

        // Trigger queue processing in the background
        scope.launch { processQueue(currentUserId) }

        return optimisticEntity.toDomain()
    }

    override suspend fun processQueue(currentUserId: String) {
        val queued = pendingMessageDao.getQueued()
        for (pending in queued) {
            processSingleMessage(pending, currentUserId)
        }
    }

    override suspend fun retry(localId: String, currentUserId: String): Boolean {
        pendingMessageDao.retryNow(localId)
        messageDao.updateStatus(localId, MessageDeliveryStatus.PENDING.toRaw())
        processQueue(currentUserId)
        return true
    }

    private suspend fun processSingleMessage(
        pending: PendingMessageEntity,
        currentUserId: String,
    ) {
        pendingMessageDao.setStatus(pending.localId, "sending")

        messageApiRepository.sendMessage(
            conversationId = pending.conversationId,
            content = pending.content,
            messageType = pending.messageType,
        ).fold(
            onSuccess = { response ->
                Timber.d("Message sent: localId=%s, serverId=%s", pending.localId, response.id)

                // Replace optimistic message with server-confirmed one
                val confirmedEntity = response.toEntity(currentUserId)
                messageDao.replaceOptimistic(pending.localId, confirmedEntity)

                // Update conversation's last message with server data
                conversationDao.updateLastMessage(
                    conversationId = pending.conversationId,
                    msgId = response.id,
                    content = response.content,
                    senderId = response.senderId,
                    timestamp = response.createdAt.orEmpty(),
                    status = response.status,
                )

                // Remove from outbox
                pendingMessageDao.delete(pending.localId)

                _events.tryEmit(
                    MessagingEvent.MessageSendConfirmed(
                        conversationId = pending.conversationId,
                        localId = pending.localId,
                        serverId = response.id,
                    )
                )
            },
            onFailure = { exception ->
                Timber.e("Message send failed: localId=%s, error=%s", pending.localId, exception.throwable.message)

                if (pending.retryCount < pending.maxRetries) {
                    // Back off and leave as queued for next retry
                    val backoffMs = INITIAL_BACKOFF_MS * (1L shl pending.retryCount)
                    pendingMessageDao.requeue(pending.localId)
                    delay(backoffMs.coerceAtMost(MAX_BACKOFF_MS))
                } else {
                    // Exhausted retries — mark as failed
                    pendingMessageDao.setStatus(pending.localId, "failed")
                    messageDao.updateStatus(pending.localId, MessageDeliveryStatus.FAILED.toRaw())

                    _events.tryEmit(
                        MessagingEvent.MessageSendFailed(
                            conversationId = pending.conversationId,
                            localId = pending.localId,
                            error = exception.throwable.message ?: "Unknown error",
                        )
                    )
                }
            },
        )
    }

    override suspend fun clear() {
        pendingMessageDao.deleteAll()
    }

    private companion object {
        const val INITIAL_BACKOFF_MS = 1_000L
        const val MAX_BACKOFF_MS = 30_000L
    }
}
