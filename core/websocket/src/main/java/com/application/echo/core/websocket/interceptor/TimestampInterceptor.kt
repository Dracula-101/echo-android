package com.application.echo.core.websocket.interceptor

import com.application.echo.core.websocket.model.WebSocketMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import timber.log.Timber
import javax.inject.Inject

/**
 * Interceptor that injects a `"timestamp"` field into outbound JSON
 * text messages.
 *
 * Binary messages and non-JSON text messages pass through unchanged.
 */
internal class TimestampInterceptor @Inject constructor(
    private val gson: Gson,
) : MessageInterceptor {

    override suspend fun interceptOutbound(message: WebSocketMessage): WebSocketMessage {
        if (message !is WebSocketMessage.Text) return message

        return try {
            val jsonObject = JsonParser.parseString(message.payload).asJsonObject
            jsonObject.addProperty(TIMESTAMP_FIELD, System.currentTimeMillis())
            WebSocketMessage.Text(gson.toJson(jsonObject))
        } catch (e: Exception) {
            // Not valid JSON — pass through unchanged
            Timber.tag(TAG).v("Skipping timestamp injection: payload is not JSON")
            message
        }
    }

    override suspend fun interceptInbound(message: WebSocketMessage): WebSocketMessage = message

    private companion object {
        const val TAG = "EchoWS"
        const val TIMESTAMP_FIELD = "timestamp"
    }
}
