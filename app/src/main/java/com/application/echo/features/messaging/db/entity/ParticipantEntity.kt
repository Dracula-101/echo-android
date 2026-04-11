package com.application.echo.features.messaging.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "participants",
    primaryKeys = ["conversationId", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("conversationId"), Index("userId")],
)
data class ParticipantEntity(
    val conversationId: String,
    val userId: String,
    val displayName: String,
    val avatarUrl: String,
    val userName: String,
    val onlineStatus: String,
)
