package com.application.echo.core.websocket.reconnect

import com.application.echo.core.websocket.logging.WebSocketLogger
import com.application.echo.core.websocket.model.WebSocketException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

/**
 * Orchestrates the reconnection loop using a [ReconnectionStrategy].
 *
 * When started, it launches a coroutine that repeatedly calls
 * [onReconnect] with increasing delays until the strategy says
 * to stop or [stop] is called.
 */
internal class ReconnectionHandler @Inject constructor(
    private val strategy: ReconnectionStrategy,
    private val logger: WebSocketLogger,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var reconnectJob: Job? = null
    private val attempt = AtomicInteger(0)
    private val isRunning = AtomicBoolean(false)

    /**
     * Starts the reconnection loop.
     *
     * @param onReconnect Suspend function that performs the actual
     *                    reconnection attempt (e.g., `connection.connect()`).
     */
    fun start(onReconnect: suspend () -> Unit) {
        if (isRunning.getAndSet(true)) return

        attempt.set(0)
        reconnectJob = scope.launch {
            while (isRunning.get()) {
                val currentAttempt = attempt.getAndIncrement()
                val delayMs = strategy.nextDelay(currentAttempt)

                if (delayMs < 0) {
                    logger.logReconnect(currentAttempt, -1)
                    isRunning.set(false)
                    break
                }

                logger.logReconnect(currentAttempt + 1, delayMs)
                delay(delayMs)

                try {
                    onReconnect()
                    // If onReconnect doesn't throw, we wait for notifySuccess/notifyFailure
                    // to break or continue the loop
                    return@launch
                } catch (e: Exception) {
                    logger.logError(WebSocketException.ConnectionFailed(e, ""))
                }
            }
        }
    }

    /** Stops the reconnection loop and resets the attempt counter. */
    fun stop() {
        isRunning.set(false)
        reconnectJob?.cancel()
        reconnectJob = null
        attempt.set(0)
        strategy.reset()
    }

    /** Called when a reconnection succeeds — resets the counter. */
    fun notifySuccess() {
        isRunning.set(false)
        reconnectJob?.cancel()
        reconnectJob = null
        attempt.set(0)
        strategy.reset()
    }

    /**
     * Called when a reconnection attempt fails — the loop continues
     * if the strategy permits.
     */
    fun notifyFailure(exception: WebSocketException) {
        logger.logError(exception)
        // The loop in start() will pick up the next attempt automatically
        // if isRunning is still true.
    }
}
