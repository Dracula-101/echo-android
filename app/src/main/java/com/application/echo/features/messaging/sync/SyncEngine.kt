package com.application.echo.features.messaging.sync

import com.application.echo.features.websocket.model.SyncConversation

/**
 * Coordinates all synchronization between the server and the local Room database.
 *
 * Three sync strategies:
 * - **Initial sync**: Full fetch on first launch or after data wipe. Paginated.
 * - **Incremental sync**: Delta fetch using the last-known timestamp/cursor.
 *   Only pulls what changed since the last successful sync.
 * - **Targeted sync**: Surgical re-fetch of specific conversations, typically
 *   triggered by a WebSocket `sync.required` event when the server detects
 *   the client has fallen behind.
 */
interface SyncEngine {

    /**
     * Sync all conversations. Uses incremental sync if we have prior sync state,
     * otherwise falls back to initial sync.
     *
     * @param forceFullSync If true, ignores sync metadata and does a full fetch.
     */
    suspend fun syncConversations(forceFullSync: Boolean = false)

    /**
     * Sync messages for a specific conversation. Uses incremental sync if we have
     * prior sync state for this conversation, otherwise does an initial fetch.
     *
     * @param forceFullSync If true, ignores sync metadata and does a full fetch.
     */
    suspend fun syncMessages(conversationId: String, forceFullSync: Boolean = false)

    /**
     * Targeted sync triggered by a WebSocket `sync.required` event.
     * Re-fetches only the conversations the server flagged as stale.
     */
    suspend fun syncTargeted(conversations: List<SyncConversation>)

    /**
     * Load older messages for a conversation (pagination going backwards).
     * Used when the user scrolls up in a chat.
     *
     * @return true if more messages are available, false if we've reached the beginning.
     */
    suspend fun loadOlderMessages(conversationId: String): Boolean

    /**
     * Checks if the sync metadata for a resource indicates it's stale
     * (hasn't synced within the given threshold).
     */
    suspend fun isStale(resourceType: String, staleThresholdMs: Long = STALE_THRESHOLD_MS): Boolean

    /**
     * Reset all sync metadata. Called on logout or data wipe.
     */
    suspend fun reset()

    companion object {
        /** Default staleness threshold: 5 minutes. */
        const val STALE_THRESHOLD_MS = 5 * 60 * 1000L

        /** Resource type key for the conversation list. */
        const val RESOURCE_CONVERSATIONS = "conversations"

        /** Resource type key pattern for messages in a specific conversation. */
        fun resourceMessages(conversationId: String) = "messages:$conversationId"
    }
}
