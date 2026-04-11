package com.application.echo.features.messaging.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val conversationType: String,
    val memberCount: Int,
    val messageCount: Int,
    val unreadCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val lastMessageId: String?,
    val lastMessageContent: String?,
    val lastMessageSenderId: String?,
    val lastMessageTimestamp: String?,
    val lastMessageStatus: String?,
)
