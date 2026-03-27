package com.application.echo.features.notification.builder

import android.graphics.Bitmap
import androidx.core.app.NotificationManagerCompat
import com.application.echo.features.notification.model.NotificationData
import com.application.echo.features.notification.model.NotificationPayload
import com.application.echo.features.notification.model.NotificationType
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central dispatcher that takes a [NotificationPayload], builds the
 * appropriate [android.app.Notification], and posts it.
 *
 * Filtering logic lives here — e.g. don't show typing indicators,
 * don't show notifications for the conversation the user is viewing.
 */
@Singleton
class NotificationDispatcher @Inject constructor(
    private val factory: NotificationFactory,
    private val notificationManager: NotificationManagerCompat,
    private val activeConversationTracker: ActiveConversationTracker,
) {

    fun dispatch(payload: NotificationPayload, avatar: Bitmap? = null) {
        when (payload) {
            is NotificationPayload.Data -> dispatchData(payload.data, avatar)
            is NotificationPayload.Display -> dispatchDisplay(payload)
        }
    }

    private fun dispatchData(data: NotificationData, avatar: Bitmap?) {
        // Suppress notifications for types that shouldn't produce a heads-up.
        if (data.type == NotificationType.TYPING_INDICATOR) return

        // Suppress if the user is already viewing this conversation.
        if (activeConversationTracker.isActive(data.conversationId)) {
            Timber.d("Suppressing notification — user is viewing conversation %s", data.conversationId)
            return
        }

        val notification = factory.build(data, avatar)
        val notificationId = NotificationFactory.notificationId(data)

        try {
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            Timber.w(e, "Missing POST_NOTIFICATIONS permission")
        }
    }

    private fun dispatchDisplay(payload: NotificationPayload.Display) {
        val data = NotificationData(
            type = NotificationType.UNKNOWN,
            title = payload.title,
            body = payload.body,
            senderId = null,
            senderName = null,
            senderAvatarUrl = null,
            conversationId = null,
            messageId = null,
            groupName = null,
            imageUrl = payload.imageUrl,
            deepLink = null,
            threadKey = null,
            timestamp = System.currentTimeMillis(),
        )
        val notification = factory.build(data)
        try {
            notificationManager.notify(data.timestamp.toInt(), notification)
        } catch (e: SecurityException) {
            Timber.w(e, "Missing POST_NOTIFICATIONS permission")
        }
    }
}
