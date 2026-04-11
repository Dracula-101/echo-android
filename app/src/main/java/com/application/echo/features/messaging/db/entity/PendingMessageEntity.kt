package com.application.echo.features.messaging.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_messages")
data class PendingMessageEntity(
    @PrimaryKey val localId: String,
    val conversationId: String,
    val content: String,
    val messageType: String,
    val createdAt: String,
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val status: String = "queued",
)
