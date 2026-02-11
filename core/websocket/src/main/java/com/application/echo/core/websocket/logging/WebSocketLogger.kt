package com.application.echo.core.websocket.logging

import com.application.echo.core.websocket.config.WebSocketConfig
import com.application.echo.core.websocket.model.WebSocketException
import com.application.echo.core.websocket.model.WebSocketMessage
import timber.log.Timber
import javax.inject.Inject

/**
 * Centralized logger for the WebSocket module.
 *
 * All log output is routed through Timber with the `EchoWS` tag.
 * Payload content is only logged when [WebSocketConfig.isDebug] is `true`
 * to prevent sensitive data from leaking in production builds.
 */
class WebSocketLogger @Inject constructor(
    private val config: WebSocketConfig,
) {

    // ──────────────── Connection Lifecycle ────────────────

    fun logConnection(url: String) {
        Timber.tag(TAG).i("Connected to %s", url)
    }

    fun logDisconnection(code: Int, reason: String) {
        Timber.tag(TAG).i("Disconnected (%d): %s", code, reason)
    }

    // ──────────────── Messages ────────────────

    fun logMessageSent(message: WebSocketMessage) {
        when (message) {
            is WebSocketMessage.Text -> {
                if (config.isDebug) {
                    Timber.tag(TAG).d(">>> SENT text (%d chars): %s", message.payload.length, message.payload)
                } else {
                    Timber.tag(TAG).d(">>> SENT text (%d chars)", message.payload.length)
                }
            }
            is WebSocketMessage.Binary -> {
                Timber.tag(TAG).d(">>> SENT binary (%d bytes)", message.payload.size)
            }
        }
    }

    fun logMessageReceived(message: WebSocketMessage) {
        when (message) {
            is WebSocketMessage.Text -> {
                if (config.isDebug) {
                    Timber.tag(TAG).d("<<< RECV text (%d chars): %s", message.payload.length, message.payload)
                } else {
                    Timber.tag(TAG).d("<<< RECV text (%d chars)", message.payload.length)
                }
            }
            is WebSocketMessage.Binary -> {
                Timber.tag(TAG).d("<<< RECV binary (%d bytes)", message.payload.size)
            }
        }
    }

    // ──────────────── Errors ────────────────

    fun logError(exception: WebSocketException) {
        when (exception) {
            is WebSocketException.ConnectionFailed ->
                Timber.tag(TAG).e(exception.throwable, "Connection failed: %s", exception.message)
            is WebSocketException.MessageSendFailed ->
                Timber.tag(TAG).e(exception.throwable, "Send failed: %s", exception.message)
            is WebSocketException.ProtocolError ->
                Timber.tag(TAG).e("Protocol error (%d): %s", exception.code, exception.reason)
            is WebSocketException.Timeout ->
                Timber.tag(TAG).w("Timeout: %s", exception.message)
            is WebSocketException.SerializationError ->
                Timber.tag(TAG).e(exception.throwable, "Serialization error: %s", exception.message)
            is WebSocketException.Closed ->
                Timber.tag(TAG).i("Closed (%d): %s", exception.code, exception.reason)
            is WebSocketException.Unknown ->
                Timber.tag(TAG).e(exception.throwable, "Unknown error: %s", exception.message)
        }
    }

    // ──────────────── Reconnection ────────────────

    fun logReconnect(attempt: Int, delayMs: Long) {
        if (delayMs < 0) {
            Timber.tag(TAG).w("Reconnection exhausted after %d attempts", attempt)
        } else {
            Timber.tag(TAG).i("Reconnecting — attempt %d in %dms", attempt, delayMs)
        }
    }

    // ──────────────── Heartbeat ────────────────

    fun logHeartbeat(type: String) {
        Timber.tag(TAG).v("Heartbeat: %s", type)
    }

    private companion object {
        const val TAG = "EchoWS"
    }
}
