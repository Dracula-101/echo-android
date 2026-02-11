package com.application.echo.core.api.message

import com.application.echo.core.network.result.ApiResult

/**
 * Public contract for all messaging operations (messages + conversations).
 */
interface MessageRepository {

    // ── Messages ──

    /**
     * Fetch messages for a given conversation.
     */
    suspend fun getMessages(
        conversationId: String,
    ): ApiResult<List<MessageResponse>>

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
     * Fetch all conversations for the current user.
     */
    suspend fun getMyConversations(): ApiResult<List<ConversationResponse>>
}
