package com.application.echo.core.websocket.connection

import com.application.echo.core.websocket.model.WebSocketEvent
import com.application.echo.core.websocket.model.WebSocketMessage
import com.application.echo.core.websocket.model.WebSocketState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Low-level WebSocket connection abstraction.
 *
 * Bridges the raw OkHttp WebSocket lifecycle into reactive Kotlin
 * [Flow]-based APIs. Consumers should prefer [WebSocketSession] for
 * higher-level orchestration (reconnection, heartbeat, interceptors).
 */
interface WebSocketConnection {

    /** Stream of connection lifecycle events. */
    val events: Flow<WebSocketEvent>

    /** Current connection state. */
    val state: StateFlow<WebSocketState>

    /** `true` when the connection is in [WebSocketState.Connected]. */
    val isConnected: Boolean

    /**
     * Opens a WebSocket connection to [url] with optional [headers].
     */
    fun connect(url: String, headers: Map<String, String> = emptyMap())

    /**
     * Sends a [message] over the open connection.
     *
     * @return `true` if the message was enqueued successfully.
     */
    fun send(message: WebSocketMessage): Boolean

    /**
     * Initiates a graceful close handshake.
     *
     * @param code A WebSocket close code (default: 1000 — normal closure).
     * @param reason A human-readable close reason.
     * @return `true` if the close frame was enqueued.
     */
    fun close(code: Int = 1000, reason: String = "Client close"): Boolean

    /**
     * Immediately closes the connection, discarding any enqueued messages.
     */
    fun cancel()
}
