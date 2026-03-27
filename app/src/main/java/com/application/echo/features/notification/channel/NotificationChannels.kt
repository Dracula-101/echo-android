package com.application.echo.features.notification.channel

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.application.echo.features.notification.model.NotificationType

/**
 * Defines and registers all notification channels and groups.
 *
 * Call [createAll] once from [android.app.Application.onCreate].
 * Channels are idempotent — re-creating them updates metadata
 * without resetting user preferences.
 */
object NotificationChannels {

    // ── Group IDs ────────────────────────────────────────────────────
    private const val GROUP_MESSAGING = "echo_group_messaging"
    private const val GROUP_SOCIAL = "echo_group_social"
    private const val GROUP_CALLS = "echo_group_calls"
    private const val GROUP_SYSTEM = "echo_group_system"

    // ── Channel IDs ──────────────────────────────────────────────────
    const val MESSAGES = "echo_messages"
    const val GROUP_MESSAGES = "echo_group_messages"
    const val REACTIONS = "echo_reactions"
    const val CALLS = "echo_calls"
    const val MISSED_CALLS = "echo_missed_calls"
    const val FRIEND_REQUESTS = "echo_friend_requests"
    const val MENTIONS = "echo_mentions"
    const val SYSTEM = "echo_system"
    const val ANNOUNCEMENTS = "echo_announcements"

    /**
     * Returns the channel ID for a given [NotificationType].
     */
    fun channelFor(type: NotificationType): String = when (type) {
        NotificationType.MESSAGE -> MESSAGES
        NotificationType.GROUP_MESSAGE -> GROUP_MESSAGES
        NotificationType.MESSAGE_REACTION -> REACTIONS
        NotificationType.TYPING_INDICATOR -> MESSAGES
        NotificationType.INCOMING_CALL -> CALLS
        NotificationType.MISSED_CALL -> MISSED_CALLS
        NotificationType.FRIEND_REQUEST -> FRIEND_REQUESTS
        NotificationType.FRIEND_ACCEPTED -> FRIEND_REQUESTS
        NotificationType.MENTION -> MENTIONS
        NotificationType.SYSTEM -> SYSTEM
        NotificationType.ANNOUNCEMENT -> ANNOUNCEMENTS
        NotificationType.UNKNOWN -> SYSTEM
    }

    fun createAll(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService<NotificationManager>() ?: return

        // Groups
        manager.createNotificationChannelGroups(
            listOf(
                NotificationChannelGroup(GROUP_MESSAGING, "Messaging"),
                NotificationChannelGroup(GROUP_SOCIAL, "Social"),
                NotificationChannelGroup(GROUP_CALLS, "Calls"),
                NotificationChannelGroup(GROUP_SYSTEM, "System"),
            ),
        )

        // Channels
        manager.createNotificationChannels(
            listOf(
                channel(
                    id = MESSAGES,
                    name = "Messages",
                    importance = NotificationManager.IMPORTANCE_HIGH,
                    group = GROUP_MESSAGING,
                    description = "Direct message notifications",
                ),
                channel(
                    id = GROUP_MESSAGES,
                    name = "Group Messages",
                    importance = NotificationManager.IMPORTANCE_HIGH,
                    group = GROUP_MESSAGING,
                    description = "Group chat notifications",
                ),
                channel(
                    id = REACTIONS,
                    name = "Reactions",
                    importance = NotificationManager.IMPORTANCE_LOW,
                    group = GROUP_MESSAGING,
                    description = "Message reaction notifications",
                ),
                channel(
                    id = CALLS,
                    name = "Incoming Calls",
                    importance = NotificationManager.IMPORTANCE_MAX,
                    group = GROUP_CALLS,
                    description = "Incoming voice and video calls",
                ),
                channel(
                    id = MISSED_CALLS,
                    name = "Missed Calls",
                    importance = NotificationManager.IMPORTANCE_DEFAULT,
                    group = GROUP_CALLS,
                    description = "Missed call notifications",
                ),
                channel(
                    id = FRIEND_REQUESTS,
                    name = "Friend Requests",
                    importance = NotificationManager.IMPORTANCE_DEFAULT,
                    group = GROUP_SOCIAL,
                    description = "Friend request and acceptance notifications",
                ),
                channel(
                    id = MENTIONS,
                    name = "Mentions",
                    importance = NotificationManager.IMPORTANCE_HIGH,
                    group = GROUP_SOCIAL,
                    description = "When someone mentions you in a conversation",
                ),
                channel(
                    id = SYSTEM,
                    name = "System",
                    importance = NotificationManager.IMPORTANCE_LOW,
                    group = GROUP_SYSTEM,
                    description = "System notifications and updates",
                ),
                channel(
                    id = ANNOUNCEMENTS,
                    name = "Announcements",
                    importance = NotificationManager.IMPORTANCE_DEFAULT,
                    group = GROUP_SYSTEM,
                    description = "Product announcements and news",
                ),
            ),
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun channel(
        id: String,
        name: String,
        importance: Int,
        group: String,
        description: String,
    ): NotificationChannel {
        return NotificationChannel(id, name, importance).apply {
            this.group = group
            this.description = description
        }
    }
}
