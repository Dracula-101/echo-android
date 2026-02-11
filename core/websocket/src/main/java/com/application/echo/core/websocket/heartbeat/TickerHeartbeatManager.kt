package com.application.echo.core.websocket.heartbeat

import com.application.echo.core.websocket.config.HeartbeatConfig
import com.application.echo.core.websocket.logging.WebSocketLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.ByteString
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

/**
 * [HeartbeatManager] implementation that uses a coroutine-based ticker.
 *
 * At each [HeartbeatConfig.intervalMs] tick, it invokes the `sendPing`
 * callback and checks whether a pong was received within
 * [HeartbeatConfig.timeoutMs]. If not, [isAlive] is set to `false`.
 */
internal class TickerHeartbeatManager @Inject constructor(
    private val config: HeartbeatConfig,
    private val logger: WebSocketLogger,
) : HeartbeatManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var tickerJob: Job? = null

    private val _isAlive = MutableStateFlow(true)
    override val isAlive: StateFlow<Boolean> = _isAlive.asStateFlow()

    private val lastPongTimestamp = AtomicLong(0L)

    override fun start(sendPing: suspend (ByteString?) -> Unit) {
        if (!config.enabled) return
        stop()

        lastPongTimestamp.set(System.currentTimeMillis())
        _isAlive.value = true

        tickerJob = scope.launch {
            while (true) {
                delay(config.intervalMs)

                logger.logHeartbeat("ping")
                sendPing(null)

                // Check if the last pong is within the timeout window
                val elapsed = System.currentTimeMillis() - lastPongTimestamp.get()
                if (elapsed > config.intervalMs + config.timeoutMs) {
                    logger.logHeartbeat("timeout")
                    _isAlive.value = false
                }
            }
        }
    }

    override fun stop() {
        tickerJob?.cancel()
        tickerJob = null
        _isAlive.value = true
    }

    override fun onPongReceived(payload: ByteString?) {
        lastPongTimestamp.set(System.currentTimeMillis())
        _isAlive.value = true
        logger.logHeartbeat("pong")
    }
}
