package com.application.echo.core.websocket.model

import okio.ByteString

/**
 * Events emitted by the WebSocket connection layer.
 *
 * Consumers collect these from `Flow<WebSocketEvent>` to react to
 * connection lifecycle changes and incoming messages.
 */
sealed interface WebSocketEvent {

    /** The WebSocket handshake completed. */
    data class OnConnected(val url: String) : WebSocketEvent

    /** A message was received from the server. */
    data class OnMessage(val message: WebSocketMessage) : WebSocketEvent

    /** The connection was closed gracefully. */
    data class OnDisconnected(val code: Int, val reason: String) : WebSocketEvent

    /** An error occurred on the connection. */
    data class OnError(val exception: WebSocketException) : WebSocketEvent

    /** A reconnection attempt is starting. */
    data class OnReconnecting(val attempt: Int) : WebSocketEvent

    /** A pong frame was received in response to a ping. */
    data class OnPong(val payload: ByteString?) : WebSocketEvent

    /** The connection was lost unexpectedly (no graceful close frame). */
    data object OnConnectionLost : WebSocketEvent
}
