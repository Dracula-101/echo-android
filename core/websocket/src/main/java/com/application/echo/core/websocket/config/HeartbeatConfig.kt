package com.application.echo.core.websocket.config

/**
 * Configuration for the heartbeat (ping/pong) mechanism.
 *
 * When enabled, the client periodically sends pings and expects pongs.
 * If a pong is not received within [timeoutMs], the connection is
 * considered dead and reconnection logic is triggered.
 *
 * @property enabled Whether the heartbeat mechanism is active.
 * @property intervalMs Interval between consecutive pings.
 * @property timeoutMs Maximum time to wait for a pong before declaring the connection dead.
 */
data class HeartbeatConfig(
    val enabled: Boolean = true,
    val intervalMs: Long = 30_000L,
    val timeoutMs: Long = 10_000L,
) {
    companion object {
        /** Heartbeat disabled. */
        val NONE = HeartbeatConfig(enabled = false)

        /** Default heartbeat settings. */
        val DEFAULT = HeartbeatConfig()
    }
}
