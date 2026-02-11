package com.application.echo.core.websocket.interceptor

import com.application.echo.core.websocket.model.WebSocketMessage
import com.google.gson.Gson
import com.google.gson.JsonParser
import timber.log.Timber
import javax.inject.Inject

/**
 * Contract for supplying the current authentication token to
 * the WebSocket interceptor layer.
 *
 * This is a local interface — it intentionally does NOT depend on
 * `core:network`'s `AuthTokenProvider` to keep the module standalone.
 * Bind it in your app's Hilt module to the same token source.
 */
interface TokenProvider {

    /** Returns the latest valid auth token, or `null` if not authenticated. */
    fun getToken(): String?
}

/**
 * Interceptor that injects the current auth token into outbound JSON
 * text messages as a `"token"` field.
 *
 * If no token is available (user logged out) or the payload is not valid
 * JSON, the message passes through unchanged.
 */
internal class AuthTokenInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val gson: Gson,
) : MessageInterceptor {

    override suspend fun interceptOutbound(message: WebSocketMessage): WebSocketMessage {
        if (message !is WebSocketMessage.Text) return message

        val token = tokenProvider.getToken() ?: run {
            Timber.tag(TAG).d("No auth token available, skipping token injection")
            return message
        }

        return try {
            val jsonObject = JsonParser.parseString(message.payload).asJsonObject
            jsonObject.addProperty(TOKEN_FIELD, token)
            WebSocketMessage.Text(gson.toJson(jsonObject))
        } catch (e: Exception) {
            Timber.tag(TAG).v("Skipping token injection: payload is not JSON")
            message
        }
    }

    override suspend fun interceptInbound(message: WebSocketMessage): WebSocketMessage = message

    private companion object {
        const val TAG = "EchoWS"
        const val TOKEN_FIELD = "token"
    }
}
