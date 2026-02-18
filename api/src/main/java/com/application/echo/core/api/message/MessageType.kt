package com.application.echo.core.api.message

/**
 * Type-safe message content types.
 *
 * Used with [MessageApiRepository.sendMessage] instead of raw strings.
 *
 * ```kotlin
 * messageRepo.sendMessage(
 *     conversationId = id,
 *     content = "Hello!",
 *     messageType = MessageType.TEXT,
 * )
 * ```
 */
object MessageType {
    const val TEXT = "text"
    const val IMAGE = "image"
    const val VIDEO = "video"
    const val AUDIO = "audio"
    const val FILE = "file"
    const val LOCATION = "location"
    const val SYSTEM = "system"
}

/**
 * Type-safe conversation types.
 *
 * Used with [MessageApiRepository.createConversation] instead of raw strings.
 *
 * ```kotlin
 * messageRepo.createConversation(
 *     conversationType = ConversationType.DIRECT,
 *     participantIds = listOf(myId, otherUserId),
 * )
 * ```
 */
object ConversationType {
    const val DIRECT = "direct"
    const val GROUP = "group"
}

/**
 * Type-safe message delivery statuses.
 *
 * Compare against [MessageResponse.status].
 */
object MessageStatus {
    const val SENT = "sent"
    const val DELIVERED = "delivered"
    const val READ = "read"
    const val FAILED = "failed"
    const val PENDING = "pending"
}
