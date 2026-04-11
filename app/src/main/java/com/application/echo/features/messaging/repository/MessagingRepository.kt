package com.application.echo.features.messaging.repository

import com.application.echo.api.message.MessageResponse
import com.application.echo.features.messaging.model.Conversation
import com.application.echo.features.messaging.model.Message
import com.application.echo.features.messaging.model.MessagingEvent
import com.application.echo.features.websocket.model.SyncConversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MessagingRepository {

    // ── Reactive Queries (backed by Room flows) ──

    val conversations: Flow<List<Conversation>>

    fun messages(conversationId: String): Flow<List<Message>>

    fun typingUsers(conversationId: String): Flow<Set<String>>

    val totalUnreadCount: Flow<Int>

    fun unreadCount(conversationId: String): Flow<Int>

    val activeConversationId: StateFlow<String?>

    // ── One-shot Events ──

    val events: Flow<MessagingEvent>

    // ── Commands ──

    suspend fun refreshConversations()

    suspend fun refreshMessages(conversationId: String)

    suspend fun loadOlderMessages(conversationId: String): Boolean

    suspend fun retryMessage(localId: String): Boolean

    suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: String,
    ): Message

    suspend fun markAsRead(conversationId: String, messageIds: List<String>)

    suspend fun createConversation(
        conversationType: String,
        participantIds: List<String>,
    ): Conversation?

    fun setActiveConversation(conversationId: String?)

    // ── WebSocket event handlers ──

    suspend fun onNewMessageReceived(message: MessageResponse)

    suspend fun onMessagesRead(conversationId: String, messageIds: List<String>)

    fun onTypingEvent(conversationId: String, userId: String, isTyping: Boolean)

    fun sendTyping(conversationId: String, isTyping: Boolean)

    suspend fun onPresenceChanged(userId: String, status: String)

    suspend fun onSyncRequired(conversations: List<SyncConversation>)

    // ── Lifecycle ──

    suspend fun clear()
}
