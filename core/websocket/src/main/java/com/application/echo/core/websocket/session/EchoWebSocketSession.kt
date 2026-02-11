package com.application.echo.core.websocket.session

import com.application.echo.core.websocket.config.WebSocketConfig
import com.application.echo.core.websocket.connection.WebSocketConnection
import com.application.echo.core.websocket.heartbeat.HeartbeatManager
import com.application.echo.core.websocket.interceptor.MessageInterceptor
import com.application.echo.core.websocket.logging.WebSocketLogger
import com.application.echo.core.websocket.message.MessageSerializer
import com.application.echo.core.websocket.model.WebSocketEvent
import com.application.echo.core.websocket.model.WebSocketException
import com.application.echo.core.websocket.model.WebSocketMessage
import com.application.echo.core.websocket.model.WebSocketState
import com.application.echo.core.websocket.reconnect.ReconnectionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okio.ByteString
import javax.inject.Inject

/**
 * Default [WebSocketSession] implementation.
 *
 * Orchestrates: raw [WebSocketConnection], [ReconnectionHandler],
 * [HeartbeatManager], and a list of [MessageInterceptor]s.
 */
internal class EchoWebSocketSession @Inject constructor(
    private val config: WebSocketConfig,
    private val connection: WebSocketConnection,
    private val reconnectionHandler: ReconnectionHandler,
    private val heartbeatManager: HeartbeatManager,
    private val serializer: MessageSerializer,
    private val interceptors: @JvmSuppressWildcards Set<MessageInterceptor>,
    private val logger: WebSocketLogger,
) : WebSocketSession {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableSharedFlow<WebSocketEvent>(extraBufferCapacity = 64)

    override val events: Flow<WebSocketEvent> = _events.asSharedFlow()

    override val state: StateFlow<WebSocketState> = connection.state

    override val messages: Flow<WebSocketMessage> = events.mapNotNull { event ->
        (event as? WebSocketEvent.OnMessage)?.message
    }

    override val isConnected: Boolean get() = connection.isConnected

    init {
        observeConnectionEvents()
    }

    // ──────────────── Public API ────────────────

    override fun connect() {
        connection.connect(config.url, config.headers)
    }

    override fun disconnect(code: Int, reason: String) {
        reconnectionHandler.stop()
        heartbeatManager.stop()
        connection.close(code, reason)
    }

    override fun send(message: WebSocketMessage): Boolean {
        val intercepted = runInterceptorsOutbound(message)
        return connection.send(intercepted)
    }

    override fun <T> sendTyped(data: T, type: Class<T>): Boolean {
        return try {
            val json = serializer.serialize(data, type)
            send(WebSocketMessage.Text(json))
        } catch (e: Exception) {
            logger.logError(WebSocketException.SerializationError(e))
            false
        }
    }

    // ──────────────── Internal ────────────────

    private fun observeConnectionEvents() {
        connection.events.onEach { event ->
            val emitted = when (event) {
                is WebSocketEvent.OnConnected -> {
                    reconnectionHandler.notifySuccess()
                    startHeartbeat()
                    event
                }

                is WebSocketEvent.OnMessage -> {
                    val intercepted = runInterceptorsInbound(event.message)
                    WebSocketEvent.OnMessage(intercepted)
                }

                is WebSocketEvent.OnDisconnected -> {
                    heartbeatManager.stop()
                    event
                }

                is WebSocketEvent.OnError -> {
                    heartbeatManager.stop()
                    event
                }

                is WebSocketEvent.OnConnectionLost -> {
                    heartbeatManager.stop()
                    startReconnection()
                    event
                }

                is WebSocketEvent.OnPong -> {
                    heartbeatManager.onPongReceived(event.payload)
                    event
                }

                is WebSocketEvent.OnReconnecting -> event
            }
            _events.emit(emitted)
        }.launchIn(scope)
    }

    private fun startHeartbeat() {
        heartbeatManager.start { payload ->
            val pingPayload = payload ?: ByteString.EMPTY
            // OkHttp handles ping/pong at the protocol level via pingIntervalMs,
            // but we use the heartbeat manager for application-level health checks.
            connection.send(WebSocketMessage.Binary(pingPayload))
        }
    }

    private fun startReconnection() {
        reconnectionHandler.start {
            connection.connect(config.url, config.headers)
        }
    }

    private fun runInterceptorsOutbound(message: WebSocketMessage): WebSocketMessage {
        var result = message
        for (interceptor in interceptors) {
            scope.launch {
                result = interceptor.interceptOutbound(result)
            }
        }
        return result
    }

    private fun runInterceptorsInbound(message: WebSocketMessage): WebSocketMessage {
        var result = message
        for (interceptor in interceptors) {
            scope.launch {
                result = interceptor.interceptInbound(result)
            }
        }
        return result
    }
}
