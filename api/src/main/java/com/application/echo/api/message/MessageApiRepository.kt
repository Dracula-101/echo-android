package com.application.echo.api.message

import com.application.echo.core.network.result.ApiResult

/**
 * Public contract for all messaging operations (messages + conversations).
 */
interface MessageApiRepository {

    // ── Messages ──

    /**
     * Fetch messages for a given conversation with cursor-based pagination.
     *
     * @param before Fetch messages created before this cursor (for loading older messages).
     * @param after Fetch messages created after this cursor (for incremental sync).
     * @param limit Maximum number of messages to return.
     */
    suspend fun getMessages(
        conversationId: String,
        before: String? = null,
        after: String? = null,
        limit: Int? = null,
    ): ApiResult<MessagesResponse>

    /**
     * Sync/catch-up endpoint for fetching messages after a known message ID.
     * Used when the client reconnects after being offline.
     *
     * @param conversationId Target conversation.
     * @param lastMessageId The ID of the last message the client has. Server returns messages after this.
     * @param limit Maximum number of messages to return per page.
     */
    suspend fun syncMessages(
        conversationId: String,
        lastMessageId: String? = null,
        limit: Int? = null,
    ): ApiResult<SyncMessagesResponse>

    /**
     * Mark a message as read. The backend marks all messages up to and including
     * this message as read for the current user in the conversation.
     */
    suspend fun markAsRead(messageId: String): ApiResult<ReadReceiptResponse>

    /**
     * Send a new message.
     *
     * @param conversationId Target conversation.
     * @param content Message body text.
     * @param messageType Type tag (e.g. `"text"`, `"image"`).
     */
    suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: String,
    ): ApiResult<MessageResponse>

    // ── Conversations ──

    /**
     * Create a new conversation.
     *
     * @param conversationType Type tag (e.g. `"direct"`, `"group"`).
     * @param participantIds List of user IDs to include.
     */
    suspend fun createConversation(
        conversationType: String,
        participantIds: List<String>,
    ): ApiResult<ConversationResponse>

    /**
     * Fetch a single conversation by its ID.
     */
    suspend fun getConversation(
        conversationId: String,
    ): ApiResult<ConversationResponse>

    /**
     * Fetch conversations for the current user with optional incremental sync.
     *
     * @param updatedSince Only return conversations updated after this ISO-8601 timestamp.
     * @param limit Maximum number of conversations to return.
     * @param cursor Server-provided cursor for pagination.
     */
    suspend fun getMyConversations(
        updatedSince: String? = null,
        limit: Int? = null,
        cursor: String? = null,
    ): ApiResult<List<ConversationResponse>>
}
