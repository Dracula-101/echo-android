package com.application.echo.features.notification.model

/**
 * All push notification types the backend can send.
 *
 * Maps 1:1 to the `type` field in FCM data payloads.
 * Used to route incoming pushes to the right channel,
 * build the right notification style, and deep-link correctly.
 */
enum class NotificationType(val key: String) {

    // ── Messaging ────────────────────────────────────────────────────
    MESSAGE("message"),
    GROUP_MESSAGE("group_message"),
    MESSAGE_REACTION("message_reaction"),
    TYPING_INDICATOR("typing"),

    // ── Calls ────────────────────────────────────────────────────────
    INCOMING_CALL("incoming_call"),
    MISSED_CALL("missed_call"),

    // ── Social ───────────────────────────────────────────────────────
    FRIEND_REQUEST("friend_request"),
    FRIEND_ACCEPTED("friend_accepted"),
    MENTION("mention"),

    // ── System ───────────────────────────────────────────────────────
    SYSTEM("system"),
    ANNOUNCEMENT("announcement"),

    // ── Fallback ─────────────────────────────────────────────────────
    UNKNOWN("unknown");

    companion object {
        private val map = entries.associateBy { it.key }

        fun fromKey(key: String?): NotificationType = map[key] ?: UNKNOWN
    }
}
