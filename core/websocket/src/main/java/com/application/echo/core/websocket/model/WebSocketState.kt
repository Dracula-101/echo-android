package com.application.echo.core.websocket.model

/**
 * Represents the current state of a WebSocket connection.
 *
 * Consumers observe state transitions via `StateFlow<WebSocketState>`.
 */
sealed interface WebSocketState {

    /** No active connection. Initial state. */
    data object Disconnected : WebSocketState

    /** A connection attempt is in progress. */
    data object Connecting : WebSocketState

    /** The WebSocket handshake completed successfully. */
    data class Connected(val url: String) : WebSocketState

    /**
     * The client is attempting to re-establish a lost connection.
     *
     * @property attempt Current retry attempt (1-based).
     * @property maxRetries Maximum allowed retries from the reconnection config.
     */
    data class Reconnecting(val attempt: Int, val maxRetries: Int) : WebSocketState

    /** A graceful disconnect has been initiated but not yet completed. */
    data object Disconnecting : WebSocketState

    /** The connection failed and will not be retried automatically. */
    data class Failed(val exception: WebSocketException) : WebSocketState
}
