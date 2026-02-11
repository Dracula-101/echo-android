package com.application.echo.core.websocket.reconnect

import com.application.echo.core.websocket.model.WebSocketException

/**
 * Strategy that determines the delay between reconnection attempts
 * and whether a reconnection should be attempted.
 */
interface ReconnectionStrategy {

    /**
     * Returns the delay in milliseconds before the next reconnection
     * attempt, or `-1` to signal that no more retries should be made.
     *
     * @param attempt The current retry attempt (0-based).
     */
    fun nextDelay(attempt: Int): Long

    /**
     * Whether a reconnection should be attempted after the given
     * close or failure.
     *
     * @param closeCode The WebSocket close code (or -1 if not available).
     * @param exception The failure that triggered the reconnection, if any.
     */
    fun shouldReconnect(closeCode: Int, exception: WebSocketException? = null): Boolean

    /** Resets internal state (e.g., attempt counter). */
    fun reset()
}
