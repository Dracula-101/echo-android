package com.application.echo.features.notification.builder

import androidx.core.app.NotificationManagerCompat
import com.application.echo.features.notification.model.NotificationData
import com.application.echo.features.notification.model.NotificationPayload
import com.application.echo.features.notification.model.NotificationType
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationDispatcher @Inject constructor(
    private val factory: NotificationFactory,
    private val notificationManager: NotificationManagerCompat,
    private val activeConversationTracker: ActiveConversationTracker,
    private val imageLoader: NotificationImageLoader,
) {

    fun dispatch(payload: NotificationPayload) {
        when (payload) {
            is NotificationPayload.Data -> dispatchData(payload.data)
            is NotificationPayload.Display -> dispatchDisplay(payload)
        }
    }

    private fun dispatchData(data: NotificationData) {
        if (data.type == NotificationType.TYPING_INDICATOR) return

        if (activeConversationTracker.isActive(data.conversationId)) {
            Timber.d("Suppressing notification — user is viewing conversation %s", data.conversationId)
            return
        }

        val avatar = imageLoader.loadCircularAvatar(data.senderAvatarUrl)
        val picture = imageLoader.loadImage(data.imageUrl)

        val notification = factory.build(data, avatar, picture)
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
        val picture = imageLoader.loadImage(payload.imageUrl)
        val notification = factory.build(data, avatar = null, picture = picture)
        try {
            notificationManager.notify(data.timestamp.toInt(), notification)
        } catch (e: SecurityException) {
            Timber.w(e, "Missing POST_NOTIFICATIONS permission")
        }
    }
}
