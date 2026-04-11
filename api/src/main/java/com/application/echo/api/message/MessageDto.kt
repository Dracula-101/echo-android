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
    @SerializedName("sender_user_id")
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
    @SerializedName("unread_count")
    val unreadCount: Int? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
)

/**
 * Response for `GET /messages/sync`.
 * Cursor-based catch-up endpoint — returns messages after a known message ID.
 */
data class SyncMessagesResponse(
    @SerializedName("messages")
    val messages: List<MessageResponse>,
    @SerializedName("has_more")
    val hasMore: Boolean,
)

/**
 * Response for `POST /messages/{id}/read`.
 */
data class ReadReceiptResponse(
    @SerializedName("message_id")
    val messageId: String,
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("read_at")
    val readAt: String? = null,
)

data class ConversationParticipant(
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("display_name")
    val displayName: String? = null,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("online_status")
    val onlineStatus: String? = null,
    @SerializedName("user_avatar")
    val userAvatar: String? = null,
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("user_name")
    val userName: String? = null,
)
