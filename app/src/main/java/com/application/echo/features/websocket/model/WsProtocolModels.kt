package com.application.echo.features.websocket.model

import android.os.Build
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

/**
 * Client-to-server envelope. Every message sent to the server is wrapped in this structure.
 */
data class ClientMessage(
    @SerializedName("id") val id: String = UUID.randomUUID().toString(),
    @SerializedName("type") val type: String,
    @SerializedName("version") val version: String = PROTOCOL_VERSION,
    @SerializedName("idempotency_key") val idempotencyKey: String? = null,
    @SerializedName("client_info") val clientInfo: ClientInfo,
    @SerializedName("payload") val payload: Any? = null,
) {
    companion object {
        const val PROTOCOL_VERSION = "1.0"
    }
}

/**
 * Device and app metadata sent with every client message.
 */
data class ClientInfo(
    @SerializedName("platform") val platform: String = "Android",
    @SerializedName("app_version") val appVersion: String,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("device_model") val deviceModel: String,
    @SerializedName("os_version") val osVersion: String = Build.VERSION.RELEASE,
    @SerializedName("timezone") val timezone: String = TimeZone.getDefault().id,
    @SerializedName("locale") val locale: String = Locale.getDefault().toLanguageTag(),
)

/**
 * Server-to-client envelope. Every message received from the server follows this structure.
 */
data class ServerMessage(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("version") val version: String,
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("request_id") val requestId: String? = null,
    @SerializedName("payload") val payload: JsonObject? = null,
)

/**
 * Event types the client sends to the server.
 */
object ClientEventType {
    const val SUBSCRIBE = "subscribe"
    const val UNSUBSCRIBE = "unsubscribe"
    const val PRESENCE_UPDATE = "presence.update"
    const val PRESENCE_QUERY = "presence.query"
    const val TYPING_START = "typing.start"
    const val TYPING_STOP = "typing.stop"
    const val MARK_DELIVERED = "mark.delivered"
    const val MARK_READ = "mark.read"
    const val PING = "ping"
}

/**
 * Event types the server pushes to the client.
 */
object ServerEventType {
    const val SUBSCRIBED = "subscribed"
    const val TYPING_START = "typing.start"
    const val TYPING_STOP = "typing.stop"
    const val PRESENCE_CHANGED = "presence.changed"
    const val MESSAGE_READ = "message.read"
    const val SYNC_REQUIRED = "sync.required"
    const val ERROR = "error"
}
