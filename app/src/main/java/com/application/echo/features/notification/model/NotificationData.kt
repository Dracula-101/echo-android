package com.application.echo.features.notification.model

/**
 * Parsed FCM data payload.
 *
 * The backend sends all push data as flat `Map<String, String>`.
 * This class gives it structure so the rest of the notification
 * stack doesn't deal with raw string maps.
 */
data class NotificationData(
    val type: NotificationType,
    val title: String,
    val body: String,
    val senderId: String?,
    val senderName: String?,
    val senderAvatarUrl: String?,
    val conversationId: String?,
    val messageId: String?,
    val groupName: String?,
    val imageUrl: String?,
    val deepLink: String?,
    val threadKey: String?,
    val timestamp: Long,
) {
    companion object {

        /** Keys the backend puts in the FCM data map. */
        private const val KEY_TYPE = "type"
        private const val KEY_TITLE = "title"
        private const val KEY_BODY = "body"
        private const val KEY_SENDER_ID = "sender_id"
        private const val KEY_SENDER_NAME = "sender_name"
        private const val KEY_SENDER_AVATAR = "sender_avatar_url"
        private const val KEY_CONVERSATION_ID = "conversation_id"
        private const val KEY_MESSAGE_ID = "message_id"
        private const val KEY_GROUP_NAME = "group_name"
        private const val KEY_IMAGE_URL = "image_url"
        private const val KEY_DEEP_LINK = "deep_link"
        private const val KEY_THREAD_KEY = "thread_key"
        private const val KEY_TIMESTAMP = "timestamp"

        fun fromMap(data: Map<String, String>): NotificationData {
            val type = NotificationType.fromKey(data[KEY_TYPE])
            return NotificationData(
                type = type,
                title = data[KEY_TITLE] ?: "",
                body = data[KEY_BODY] ?: "",
                senderId = data[KEY_SENDER_ID],
                senderName = data[KEY_SENDER_NAME],
                senderAvatarUrl = data[KEY_SENDER_AVATAR],
                conversationId = data[KEY_CONVERSATION_ID],
                messageId = data[KEY_MESSAGE_ID],
                groupName = data[KEY_GROUP_NAME],
                imageUrl = data[KEY_IMAGE_URL],
                deepLink = data[KEY_DEEP_LINK],
                threadKey = data[KEY_THREAD_KEY] ?: data[KEY_CONVERSATION_ID],
                timestamp = data[KEY_TIMESTAMP]?.toLongOrNull()
                    ?: System.currentTimeMillis(),
            )
        }
    }
}
