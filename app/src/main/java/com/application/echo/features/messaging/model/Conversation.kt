package com.application.echo.features.messaging.model

data class Conversation(
    val id: String,
    val conversationType: String,
    val participants: List<Participant>,
    val lastMessage: LastMessage?,
    val memberCount: Int,
    val messageCount: Int,
    val unreadCount: Int,
    val createdAt: String,
    val updatedAt: String,
)

data class LastMessage(
    val id: String,
    val content: String?,
    val senderId: String,
    val timestamp: String,
    val status: String?,
)

data class Participant(
    val userId: String,
    val displayName: String,
    val avatarUrl: String,
    val userName: String,
    val onlineStatus: OnlineStatus,
)

enum class OnlineStatus {
    ONLINE,
    AWAY,
    OFFLINE,
    UNKNOWN;

    companion object {
        fun from(raw: String?): OnlineStatus = when (raw?.lowercase()) {
            "online" -> ONLINE
            "away" -> AWAY
            "offline" -> OFFLINE
            else -> UNKNOWN
        }
    }
}
