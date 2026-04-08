package com.application.echo.features.websocket.model

import com.google.gson.annotations.SerializedName

// ──────────────── Client Payloads ────────────────

data class SubscribePayload(
    @SerializedName("topics") val topics: List<String>,
    @SerializedName("filters") val filters: SubscribeFilters? = null,
)

data class SubscribeFilters(
    @SerializedName("conversation_ids") val conversationIds: List<String>? = null,
    @SerializedName("user_id") val userId: String? = null,
)

data class UnsubscribePayload(
    @SerializedName("topics") val topics: List<String>,
    @SerializedName("filters") val filters: UnsubscribeFilters? = null,
)

data class UnsubscribeFilters(
    @SerializedName("conversation_ids") val conversationIds: List<String>? = null,
)

data class PresenceUpdatePayload(
    @SerializedName("status") val status: String,
    @SerializedName("custom_status") val customStatus: String? = null,
    @SerializedName("device_info") val deviceInfo: PresenceDeviceInfo? = null,
)

data class PresenceDeviceInfo(
    @SerializedName("battery") val battery: String? = null,
)

data class PresenceQueryPayload(
    @SerializedName("user_ids") val userIds: List<String>,
)

data class TypingPayload(
    @SerializedName("conversation_id") val conversationId: String,
)

data class DeliveryAckPayload(
    @SerializedName("conversation_id") val conversationId: String,
    @SerializedName("message_ids") val messageIds: List<String>,
    @SerializedName("delivered_at") val deliveredAt: String,
)

data class ReadAckPayload(
    @SerializedName("conversation_id") val conversationId: String,
    @SerializedName("message_ids") val messageIds: List<String>,
    @SerializedName("read_at") val readAt: String,
)

data class PingPayload(
    @SerializedName("client_timestamp") val clientTimestamp: String,
    @SerializedName("network_info") val networkInfo: PingNetworkInfo? = null,
)

data class PingNetworkInfo(
    @SerializedName("connection_type") val connectionType: String,
)

// ──────────────── Server Payloads ────────────────

data class SubscribedPayload(
    @SerializedName("topics") val topics: List<String>,
    @SerializedName("subscription_count") val subscriptionCount: Int,
    @SerializedName("subscriptions") val subscriptions: List<SubscriptionInfo>,
)

data class SubscriptionInfo(
    @SerializedName("topic") val topic: String,
    @SerializedName("resource_id") val resourceId: String? = null,
    @SerializedName("active") val active: Boolean,
)

data class TypingEventPayload(
    @SerializedName("user_id") val userId: String,
    @SerializedName("conversation_id") val conversationId: String,
    @SerializedName("is_typing") val isTyping: Boolean,
    @SerializedName("timestamp") val timestamp: String,
)

data class PresenceChangedPayload(
    @SerializedName("user_id") val userId: String,
    @SerializedName("old_status") val oldStatus: String,
    @SerializedName("new_status") val newStatus: String,
    @SerializedName("custom_status") val customStatus: String? = null,
    @SerializedName("changed_at") val changedAt: String,
)

data class MessageReadPayload(
    @SerializedName("user_id") val userId: String,
    @SerializedName("conversation_id") val conversationId: String,
    @SerializedName("message_ids") val messageIds: List<String>,
    @SerializedName("read_at") val readAt: String,
)

data class SyncRequiredPayload(
    @SerializedName("conversations") val conversations: List<SyncConversation>,
)

data class SyncConversation(
    @SerializedName("conversation_id") val conversationId: String,
    @SerializedName("unread_count") val unreadCount: Int,
    @SerializedName("last_message_id") val lastMessageId: String,
    @SerializedName("last_message_at") val lastMessageAt: String,
)

data class WsErrorPayload(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("retryable") val retryable: Boolean,
    @SerializedName("request_id") val requestId: String? = null,
)
