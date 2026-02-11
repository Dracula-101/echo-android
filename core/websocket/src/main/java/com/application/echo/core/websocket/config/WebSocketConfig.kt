package com.application.echo.core.websocket.config

/**
 * Primary configuration for establishing a WebSocket connection.
 *
 * @property url The WebSocket endpoint URL (ws:// or wss://).
 * @property pingIntervalMs Interval between automatic pings sent by OkHttp (0 to disable).
 * @property connectTimeoutMs TCP connection timeout in milliseconds.
 * @property readTimeoutMs Read timeout in milliseconds.
 * @property writeTimeoutMs Write timeout in milliseconds.
 * @property headers Additional headers sent during the WebSocket handshake.
 * @property isDebug When `true`, enables verbose payload logging.
 */
data class WebSocketConfig(
    val url: String,
    val pingIntervalMs: Long = 30_000L,
    val connectTimeoutMs: Long = 30_000L,
    val readTimeoutMs: Long = 30_000L,
    val writeTimeoutMs: Long = 30_000L,
    val headers: Map<String, String> = emptyMap(),
    val isDebug: Boolean = false,
) {
    companion object {
        /** Sensible defaults pointing to an empty URL (must be overridden). */
        val DEFAULT = WebSocketConfig(url = "")
    }
}
