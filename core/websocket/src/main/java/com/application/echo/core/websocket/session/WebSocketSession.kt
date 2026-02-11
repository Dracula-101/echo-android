package com.application.echo.core.websocket.session

import com.application.echo.core.websocket.model.WebSocketEvent
import com.application.echo.core.websocket.model.WebSocketMessage
import com.application.echo.core.websocket.model.WebSocketState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * High-level WebSocket session — the primary API surface for consumers.
 *
 * A session orchestrates the full lifecycle: connection management,
 * automatic reconnection, heartbeat, message interceptors, and typed
 * serialization. Prefer this over [WebSocketConnection] unless you
 * need raw, unmanaged access.
 *
 * ```kotlin
 * session.connect()
 * session.messages.collect { msg -> … }
 * session.send(WebSocketMessage.Text(json))
 * session.disconnect()
 * ```
 */
interface WebSocketSession {

    /** Stream of all connection lifecycle events. */
    val events: Flow<WebSocketEvent>

    /** Current connection state. */
    val state: StateFlow<WebSocketState>

    /** Convenience flow that emits only [WebSocketMessage]s (filtered from [events]). */
    val messages: Flow<WebSocketMessage>

    /** `true` when the session is in the [WebSocketState.Connected] state. */
    val isConnected: Boolean

    /** Opens the WebSocket connection using the configured URL and headers. */
    fun connect()

    /**
     * Initiates a graceful disconnect.
     *
     * @param code WebSocket close code (default: 1000).
     * @param reason Human-readable close reason.
     */
    fun disconnect(code: Int = 1000, reason: String = "Client close")

    /**
     * Sends a raw [WebSocketMessage] (text or binary).
     *
     * @return `true` if the message was enqueued successfully.
     */
    fun send(message: WebSocketMessage): Boolean

    /**
     * Serializes [data] and sends it as a text message.
     *
     * @return `true` if the message was enqueued successfully.
     */
    fun <T> sendTyped(data: T, type: Class<T>): Boolean
}

/** Reified convenience for [WebSocketSession.sendTyped]. */
inline fun <reified T> WebSocketSession.sendTyped(data: T): Boolean =
    sendTyped(data, T::class.java)
