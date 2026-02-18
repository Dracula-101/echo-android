package com.application.echo.core.api.message

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
    ): ApiResult<List<MessageResponse>> = api.getMessages(
        conversationId = conversationId,
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

    override suspend fun getMyConversations(): ApiResult<List<ConversationResponse>> =
        api.getMyConversations().toApiResult()
}
