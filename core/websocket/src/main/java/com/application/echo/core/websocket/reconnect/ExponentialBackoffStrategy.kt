package com.application.echo.core.websocket.reconnect

import com.application.echo.core.websocket.config.ReconnectionConfig
import com.application.echo.core.websocket.model.WebSocketCloseCode
import com.application.echo.core.websocket.model.WebSocketException
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

/**
 * Default [ReconnectionStrategy] using exponential back-off with jitter.
 *
 * Delay formula: `min(initialDelay × multiplier^attempt, maxDelay) + jitter`
 *
 * Stops retrying after [ReconnectionConfig.maxRetries] consecutive failures.
 */
internal class ExponentialBackoffStrategy @Inject constructor(
    private val config: ReconnectionConfig,
) : ReconnectionStrategy {

    override fun nextDelay(attempt: Int): Long {
        if (!config.enabled || attempt >= config.maxRetries) return -1L

        val exponentialDelay = config.initialDelayMs * config.backoffMultiplier.pow(attempt.toDouble())
        val capped = min(exponentialDelay.toLong(), config.maxDelayMs)
        val jitter = Random.nextLong(0, (capped * JITTER_FACTOR).toLong().coerceAtLeast(1))

        return capped + jitter
    }

    override fun shouldReconnect(closeCode: Int, exception: WebSocketException?): Boolean {
        if (!config.enabled) return false

        // Never retry a normal closure unless config says so
        val code = WebSocketCloseCode(closeCode)
        if (code.isNormal && !config.reconnectOnClose) return false

        // Retry on retryable close codes
        if (code.isRetryable) return true

        // Retry on failures if configured
        if (exception != null && config.reconnectOnFailure) return true

        return config.reconnectOnClose
    }

    override fun reset() {
        // Stateless — no internal counters to reset.
        // Attempt tracking lives in ReconnectionHandler.
    }

    private companion object {
        const val JITTER_FACTOR = 0.1
    }
}
