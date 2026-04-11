package com.application.echo.features.messaging.model

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val messageType: String,
    val status: MessageDeliveryStatus,
    val createdAt: String,
    val updatedAt: String,
    val isOutgoing: Boolean,
    val localId: String? = null,
)

enum class MessageDeliveryStatus {
    PENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED;

    fun toRaw(): String = name.lowercase()

    companion object {
        fun from(raw: String?): MessageDeliveryStatus = when (raw?.lowercase()) {
            "sent" -> SENT
            "delivered" -> DELIVERED
            "read" -> READ
            "failed" -> FAILED
            else -> PENDING
        }
    }
}
