package com.application.echo.features.notification.action

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import androidx.core.content.getSystemService
import timber.log.Timber

/**
 * Handles notification action button taps:
 * - **Reply**: extracts the inline reply text from [RemoteInput]
 * - **Mark as Read**: dismisses the notification
 *
 * Both actions dismiss the notification after handling.
 * Actual backend calls (send message, mark read) will be wired
 * when the message repository is integrated.
 */
class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)

        when (intent.action) {
            ACTION_REPLY -> {
                val replyText = RemoteInput.getResultsFromIntent(intent)
                    ?.getCharSequence(KEY_REPLY_TEXT)
                    ?.toString()

                if (replyText.isNullOrBlank()) {
                    Timber.w("Reply action received with no text")
                    return
                }

                Timber.d(
                    "Reply to conversation %s: %s",
                    conversationId,
                    replyText,
                )

                // TODO: send reply via MessageRepository when available
                // For now, just dismiss the notification.
                dismissNotification(context, notificationId)
            }

            ACTION_MARK_READ -> {
                Timber.d("Mark as read: conversation %s", conversationId)

                // TODO: call mark-read API via MessageRepository when available
                dismissNotification(context, notificationId)
            }
        }
    }

    private fun dismissNotification(context: Context, notificationId: Int) {
        if (notificationId == -1) return
        context.getSystemService<NotificationManager>()?.cancel(notificationId)
    }

    companion object {
        const val ACTION_REPLY = "com.application.echo.ACTION_REPLY"
        const val ACTION_MARK_READ = "com.application.echo.ACTION_MARK_READ"

        const val KEY_REPLY_TEXT = "key_reply_text"
        const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
