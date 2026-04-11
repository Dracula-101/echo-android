package com.application.echo.features.messaging.outbox

import com.application.echo.features.messaging.model.Message
import com.application.echo.features.messaging.model.MessagingEvent
import kotlinx.coroutines.flow.Flow

interface MessageOutbox {
    val events: Flow<MessagingEvent>

    suspend fun enqueue(
        conversationId: String,
        content: String,
        messageType: String,
        currentUserId: String,
    ): Message

    suspend fun processQueue(currentUserId: String)

    suspend fun retry(localId: String, currentUserId: String): Boolean

    suspend fun clear()
}
