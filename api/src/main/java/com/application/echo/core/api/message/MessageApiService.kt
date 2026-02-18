package com.application.echo.core.api.message

import com.application.echo.core.api.common.ApiConstants
import com.application.echo.core.api.common.HealthResponse
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
    ): NetworkResponse<List<MessageResponse>>

    @POST(ApiConstants.MESSAGES)
    suspend fun sendMessage(
        @Body request: SendMessageRequest,
    ): NetworkResponse<MessageResponse>

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
    suspend fun getMyConversations(): NetworkResponse<List<ConversationResponse>>
}
