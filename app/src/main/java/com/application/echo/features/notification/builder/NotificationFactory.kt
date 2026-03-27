package com.application.echo.features.notification.builder

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.application.echo.MainActivity
import com.application.echo.R
import com.application.echo.features.notification.action.NotificationActionReceiver
import com.application.echo.features.notification.channel.NotificationChannels
import com.application.echo.features.notification.model.NotificationData
import com.application.echo.features.notification.model.NotificationType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds [Notification] objects from [NotificationData].
 *
 * Each notification type gets its own style:
 * - Messages → [NotificationCompat.MessagingStyle] with conversation grouping
 * - Calls → full-screen intent style
 * - Social → simple big-text style
 * - System → minimal default style
 */
@Singleton
class NotificationFactory @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun build(data: NotificationData, avatar: Bitmap? = null): Notification {
        return when (data.type) {
            NotificationType.MESSAGE,
            NotificationType.GROUP_MESSAGE,
            -> buildMessageNotification(data, avatar)

            NotificationType.MESSAGE_REACTION -> buildReactionNotification(data)

            NotificationType.INCOMING_CALL -> buildCallNotification(data)
            NotificationType.MISSED_CALL -> buildMissedCallNotification(data)

            NotificationType.FRIEND_REQUEST,
            NotificationType.FRIEND_ACCEPTED,
            -> buildSocialNotification(data)

            NotificationType.MENTION -> buildMentionNotification(data)

            NotificationType.TYPING_INDICATOR,
            NotificationType.SYSTEM,
            NotificationType.ANNOUNCEMENT,
            NotificationType.UNKNOWN,
            -> buildDefaultNotification(data)
        }
    }

    // ── Message ──────────────────────────────────────────────────────

    private fun buildMessageNotification(
        data: NotificationData,
        avatar: Bitmap?,
    ): Notification {
        val channelId = NotificationChannels.channelFor(data.type)
        val person = Person.Builder()
            .setName(data.senderName ?: "Someone")
            .apply { if (avatar != null) setIcon(androidx.core.graphics.drawable.IconCompat.createWithBitmap(avatar)) }
            .build()

        val style = NotificationCompat.MessagingStyle(mePerson())
            .setConversationTitle(
                if (data.type == NotificationType.GROUP_MESSAGE) data.groupName else null,
            )
            .setGroupConversation(data.type == NotificationType.GROUP_MESSAGE)
            .addMessage(data.body, data.timestamp, person)

        return baseBuilder(channelId, data)
            .setStyle(style)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .addAction(replyAction(data))
            .addAction(markReadAction(data))
            .build()
    }

    // ── Reaction ─────────────────────────────────────────────────────

    private fun buildReactionNotification(data: NotificationData): Notification {
        val channelId = NotificationChannels.channelFor(data.type)
        return baseBuilder(channelId, data)
            .setContentText("${data.senderName ?: "Someone"} reacted: ${data.body}")
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .build()
    }

    // ── Calls ────────────────────────────────────────────────────────

    private fun buildCallNotification(data: NotificationData): Notification {
        val channelId = NotificationChannels.channelFor(data.type)
        return baseBuilder(channelId, data)
            .setContentText(data.body.ifEmpty { "${data.senderName ?: "Someone"} is calling…" })
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setFullScreenIntent(contentPendingIntent(data), true)
            .build()
    }

    private fun buildMissedCallNotification(data: NotificationData): Notification {
        val channelId = NotificationChannels.channelFor(data.type)
        return baseBuilder(channelId, data)
            .setContentText(data.body.ifEmpty { "Missed call from ${data.senderName ?: "Unknown"}" })
            .setCategory(NotificationCompat.CATEGORY_MISSED_CALL)
            .build()
    }

    // ── Social ───────────────────────────────────────────────────────

    private fun buildSocialNotification(data: NotificationData): Notification {
        val channelId = NotificationChannels.channelFor(data.type)
        return baseBuilder(channelId, data)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data.body))
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .build()
    }

    // ── Mentions ─────────────────────────────────────────────────────

    private fun buildMentionNotification(data: NotificationData): Notification {
        val channelId = NotificationChannels.channelFor(data.type)
        return baseBuilder(channelId, data)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data.body))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .addAction(replyAction(data))
            .build()
    }

    // ── Default ──────────────────────────────────────────────────────

    private fun buildDefaultNotification(data: NotificationData): Notification {
        val channelId = NotificationChannels.channelFor(data.type)
        return baseBuilder(channelId, data)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data.body))
            .build()
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private fun baseBuilder(
        channelId: String,
        data: NotificationData,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(data.title.ifEmpty { "Echo" })
            .setContentText(data.body)
            .setAutoCancel(true)
            .setWhen(data.timestamp)
            .setShowWhen(true)
            .setContentIntent(contentPendingIntent(data))
            .setGroup(data.threadKey)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    private fun mePerson(): Person = Person.Builder().setName("Me").build()

    private fun contentPendingIntent(data: NotificationData): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data.conversationId?.let { putExtra(EXTRA_CONVERSATION_ID, it) }
            data.deepLink?.let { putExtra(EXTRA_DEEP_LINK, it) }
            putExtra(EXTRA_NOTIFICATION_TYPE, data.type.key)
        }
        return PendingIntent.getActivity(
            context,
            data.conversationId?.hashCode() ?: System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun replyAction(data: NotificationData): NotificationCompat.Action {
        val remoteInput = RemoteInput.Builder(NotificationActionReceiver.KEY_REPLY_TEXT)
            .setLabel("Reply")
            .build()

        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_REPLY
            putExtra(NotificationActionReceiver.EXTRA_CONVERSATION_ID, data.conversationId)
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId(data))
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            data.conversationId?.hashCode() ?: 0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        return NotificationCompat.Action.Builder(
            R.drawable.ic_notification,
            "Reply",
            pendingIntent,
        ).addRemoteInput(remoteInput)
            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)
            .setShowsUserInterface(false)
            .build()
    }

    private fun markReadAction(data: NotificationData): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_MARK_READ
            putExtra(NotificationActionReceiver.EXTRA_CONVERSATION_ID, data.conversationId)
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId(data))
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (data.conversationId?.hashCode() ?: 0) + 1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        return NotificationCompat.Action.Builder(
            R.drawable.ic_notification,
            "Mark as Read",
            pendingIntent,
        ).setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MARK_AS_READ)
            .setShowsUserInterface(false)
            .build()
    }

    companion object {
        const val EXTRA_CONVERSATION_ID = "conversation_id"
        const val EXTRA_DEEP_LINK = "deep_link"
        const val EXTRA_NOTIFICATION_TYPE = "notification_type"

        fun notificationId(data: NotificationData): Int =
            data.conversationId?.hashCode() ?: data.timestamp.toInt()
    }
}
