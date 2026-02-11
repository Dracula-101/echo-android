package com.application.echo.core.api.message

import com.google.gson.annotations.SerializedName

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Request Bodies
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Request body for `POST /messages`.
 */
data class SendMessageRequest(
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("message_type")
    val messageType: String,
)

/**
 * Request body for `POST /messages/conversations`.
 */
data class CreateConversationRequest(
    @SerializedName("conversation_type")
    val conversationType: String,
    @SerializedName("participant_ids")
    val participantIds: List<String>,
)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Response Bodies
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Response `data` for `POST /messages` and items in `GET /messages`.
 */
data class MessageResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("sender_id")
    val senderId: String,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("message_type")
    val messageType: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
)

/**
 * Response `data` for `POST /messages/conversations`, `GET /messages/conversations/{id}`,
 * and items in `GET /messages/conversations/me`.
 */
data class ConversationResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("conversation_type")
    val conversationType: String? = null,
    @SerializedName("participant_ids")
    val participantIds: List<String>? = null,
    @SerializedName("last_message")
    val lastMessage: MessageResponse? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
)
