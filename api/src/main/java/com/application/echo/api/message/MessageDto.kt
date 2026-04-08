package com.application.echo.api.message

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

data class MessagesResponse(
    @SerializedName("has_more")
    val hasMore: Boolean,
    @SerializedName("messages")
    val messages: List<MessageResponse>,
    @SerializedName("limit")
    val limit: Int,
)
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
    @SerializedName("participants")
    val participants: List<ConversationParticipant>? = null,
    @SerializedName("last_message")
    val lastMessage: MessageResponse? = null,
    @SerializedName("member_count")
    val memberCount: Int? = null,
    @SerializedName("message_count")
    val messageCount: Int? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
)

data class ConversationParticipant(
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("online_status")
    val onlineStatus: String,
    @SerializedName("user_avatar")
    val userAvatar: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_name")
    val userName: String
)
