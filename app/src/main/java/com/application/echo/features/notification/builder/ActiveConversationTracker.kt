package com.application.echo.features.notification.builder

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks which conversation (if any) the user is currently viewing.
 *
 * Call [set] when navigating into a conversation screen and
 * [clear] when leaving. The [NotificationDispatcher] uses this
 * to suppress notifications for the active conversation.
 */
@Singleton
class ActiveConversationTracker @Inject constructor() {

    @Volatile
    private var activeConversationId: String? = null

    fun set(conversationId: String) {
        activeConversationId = conversationId
    }

    fun clear() {
        activeConversationId = null
    }

    fun isActive(conversationId: String?): Boolean {
        if (conversationId == null) return false
        return activeConversationId == conversationId
    }
}
