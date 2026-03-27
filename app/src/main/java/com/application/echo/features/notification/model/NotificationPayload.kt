package com.application.echo.features.notification.model

import com.google.firebase.messaging.RemoteMessage

/**
 * Wraps either a Firebase notification payload or a parsed data payload
 * into a single type the notification stack can work with uniformly.
 */
sealed interface NotificationPayload {

    /** Firebase Console "notification" messages (title + body in the notification key). */
    data class Display(
        val title: String,
        val body: String,
        val channelId: String?,
        val imageUrl: String?,
    ) : NotificationPayload

    /** Backend data-only messages — fully parsed into [NotificationData]. */
    data class Data(val data: NotificationData) : NotificationPayload

    companion object {
        fun from(message: RemoteMessage): NotificationPayload? {
            // Prefer data payload when present.
            if (message.data.isNotEmpty()) {
                val parsed = NotificationData.fromMap(message.data)
                if (parsed.body.isNotEmpty()) return Data(parsed)
            }

            // Fall back to Firebase notification payload.
            val notification = message.notification ?: return null
            return Display(
                title = notification.title ?: "Echo",
                body = notification.body ?: "",
                channelId = notification.channelId,
                imageUrl = notification.imageUrl?.toString(),
            )
        }
    }
}
