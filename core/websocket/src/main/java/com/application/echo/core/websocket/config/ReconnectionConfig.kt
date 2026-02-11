package com.application.echo.core.websocket.config

/**
 * Configuration for automatic reconnection after unexpected disconnects.
 *
 * @property enabled Whether automatic reconnection is enabled.
 * @property maxRetries Maximum number of consecutive retry attempts before giving up.
 * @property initialDelayMs Delay before the first reconnection attempt.
 * @property maxDelayMs Upper bound for the back-off delay.
 * @property backoffMultiplier Multiplier applied to the delay after each failed attempt.
 * @property reconnectOnFailure Reconnect when the connection fails with an error.
 * @property reconnectOnClose Reconnect when the server closes the connection.
 */
data class ReconnectionConfig(
    val enabled: Boolean = true,
    val maxRetries: Int = 10,
    val initialDelayMs: Long = 1_000L,
    val maxDelayMs: Long = 60_000L,
    val backoffMultiplier: Double = 2.0,
    val reconnectOnFailure: Boolean = true,
    val reconnectOnClose: Boolean = true,
) {
    companion object {
        /** No automatic reconnection. */
        val NONE = ReconnectionConfig(enabled = false)

        /** Default reconnection policy. */
        val DEFAULT = ReconnectionConfig()

        /** Aggressive reconnection: more retries, shorter initial delay. */
        val AGGRESSIVE = ReconnectionConfig(
            maxRetries = 20,
            initialDelayMs = 500L,
            maxDelayMs = 30_000L,
            backoffMultiplier = 1.5,
        )
    }
}
