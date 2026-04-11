package com.application.echo.api.message

import com.application.echo.api.common.ApiConstants
import com.application.echo.api.common.HealthResponse
import com.application.echo.core.network.model.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service definition for the Messages API.
 *
 * Internal — consumers use [MessageApiRepository] instead.
 */
internal interface MessageApiService {

    // ── Messages ──

    @GET(ApiConstants.MESSAGES)
    suspend fun getMessages(
        @Query("conversation_id") conversationId: String,
        @Query("before") before: String? = null,
        @Query("after") after: String? = null,
        @Query("limit") limit: Int? = null,
    ): NetworkResponse<MessagesResponse>

    @GET(ApiConstants.MESSAGES_SYNC)
    suspend fun syncMessages(
        @Query("conversation_id") conversationId: String,
        @Query("last_message_id") lastMessageId: String? = null,
        @Query("limit") limit: Int? = null,
    ): NetworkResponse<SyncMessagesResponse>

    @POST(ApiConstants.MESSAGES)
    suspend fun sendMessage(
        @Body request: SendMessageRequest,
    ): NetworkResponse<MessageResponse>

    @POST(ApiConstants.MESSAGE_READ)
    suspend fun markAsRead(
        @Path("message_id") messageId: String,
    ): NetworkResponse<ReadReceiptResponse>

    @GET(ApiConstants.MESSAGES_HEALTH)
    suspend fun health(): NetworkResponse<HealthResponse>

    // ── Conversations ──

    @POST(ApiConstants.CONVERSATIONS)
    suspend fun createConversation(
        @Body request: CreateConversationRequest,
    ): NetworkResponse<ConversationResponse>

    @GET(ApiConstants.CONVERSATION_BY_ID)
    suspend fun getConversation(
        @Path("conversation_id") conversationId: String,
    ): NetworkResponse<ConversationResponse>

    @GET(ApiConstants.MY_CONVERSATIONS)
    suspend fun getMyConversations(
        @Query("updated_since") updatedSince: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("cursor") cursor: String? = null,
    ): NetworkResponse<List<ConversationResponse>>
}
