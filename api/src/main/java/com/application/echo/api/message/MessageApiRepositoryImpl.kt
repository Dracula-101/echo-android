package com.application.echo.api.message

import com.application.echo.core.network.result.ApiResult
import com.application.echo.core.network.result.toApiResult
import javax.inject.Inject

/**
 * Default [MessageApiRepository] backed by [MessageApiService].
 */
internal class MessageApiRepositoryImpl @Inject constructor(
    private val api: MessageApiService,
) : MessageApiRepository {

    // ── Messages ──

    override suspend fun getMessages(
        conversationId: String,
        before: String?,
        after: String?,
        limit: Int?,
    ): ApiResult<MessagesResponse> = api.getMessages(
        conversationId = conversationId,
        before = before,
        after = after,
        limit = limit,
    ).toApiResult()

    override suspend fun syncMessages(
        conversationId: String,
        lastMessageId: String?,
        limit: Int?,
    ): ApiResult<SyncMessagesResponse> = api.syncMessages(
        conversationId = conversationId,
        lastMessageId = lastMessageId,
        limit = limit,
    ).toApiResult()

    override suspend fun markAsRead(
        messageId: String,
    ): ApiResult<ReadReceiptResponse> = api.markAsRead(
        messageId = messageId,
    ).toApiResult()

    override suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: String,
    ): ApiResult<MessageResponse> = api.sendMessage(
        request = SendMessageRequest(
            conversationId = conversationId,
            content = content,
            messageType = messageType,
        ),
    ).toApiResult()

    // ── Conversations ──

    override suspend fun createConversation(
        conversationType: String,
        participantIds: List<String>,
    ): ApiResult<ConversationResponse> = api.createConversation(
        request = CreateConversationRequest(
            conversationType = conversationType,
            participantIds = participantIds,
        ),
    ).toApiResult()

    override suspend fun getConversation(
        conversationId: String,
    ): ApiResult<ConversationResponse> = api.getConversation(
        conversationId = conversationId,
    ).toApiResult()

    override suspend fun getMyConversations(
        updatedSince: String?,
        limit: Int?,
        cursor: String?,
    ): ApiResult<List<ConversationResponse>> = api.getMyConversations(
        updatedSince = updatedSince,
        limit = limit,
        cursor = cursor,
    ).toApiResult()
}
