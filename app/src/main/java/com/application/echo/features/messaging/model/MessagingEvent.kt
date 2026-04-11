package com.application.echo.features.messaging.model

sealed interface MessagingEvent {
    data class NewMessageReceived(
        val conversationId: String,
        val messageId: String,
    ) : MessagingEvent

    data class MessageSendFailed(
        val conversationId: String,
        val localId: String,
        val error: String,
    ) : MessagingEvent

    data class MessageSendConfirmed(
        val conversationId: String,
        val localId: String,
        val serverId: String,
    ) : MessagingEvent

    data class ConversationCreated(
        val conversationId: String,
    ) : MessagingEvent
}
