package com.application.echo.core.websocket.connection

import com.application.echo.core.websocket.logging.WebSocketLogger
import com.application.echo.core.websocket.model.WebSocketEvent
import com.application.echo.core.websocket.model.WebSocketException
import com.application.echo.core.websocket.model.WebSocketMessage
import com.application.echo.core.websocket.model.WebSocketState
import com.application.echo.core.websocket.qualifier.WebSocketOkHttp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

/**
 * OkHttp-backed implementation of [WebSocketConnection].
 *
 * Bridges OkHttp [WebSocketListener] callbacks into a Kotlin
 * [callbackFlow] producing [WebSocketEvent]s and maintains a
 * [StateFlow] of the connection state.
 */
internal class OkHttpWebSocketConnection @Inject constructor(
    @WebSocketOkHttp private val okHttpClient: OkHttpClient,
    private val webSocketFactory: WebSocketFactory,
    private val logger: WebSocketLogger,
) : WebSocketConnection {

    private val _state = MutableStateFlow<WebSocketState>(WebSocketState.Disconnected)
    override val state: StateFlow<WebSocketState> = _state.asStateFlow()

    override val isConnected: Boolean
        get() = _state.value is WebSocketState.Connected

    private val webSocketRef = AtomicReference<WebSocket?>(null)

    override val events: Flow<WebSocketEvent> = callbackFlow {
        // The listener is set up once via callbackFlow; the actual WebSocket
        // instance is created in connect() and swapped into webSocketRef.
        val listener = object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                val url = response.request.url.toString()
                _state.value = WebSocketState.Connected(url)
                logger.logConnection(url)
                trySend(WebSocketEvent.OnConnected(url))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val message = WebSocketMessage.Text(text)
                logger.logMessageReceived(message)
                trySend(WebSocketEvent.OnMessage(message))
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                val message = WebSocketMessage.Binary(bytes)
                logger.logMessageReceived(message)
                trySend(WebSocketEvent.OnMessage(message))
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                _state.value = WebSocketState.Disconnecting
                webSocket.close(code, reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _state.value = WebSocketState.Disconnected
                logger.logDisconnection(code, reason)
                trySend(WebSocketEvent.OnDisconnected(code, reason))
                webSocketRef.set(null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                val url = response?.request?.url?.toString() ?: ""
                val exception = WebSocketException.ConnectionFailed(t, url)
                _state.value = WebSocketState.Failed(exception)
                logger.logError(exception)
                trySend(WebSocketEvent.OnError(exception))
                trySend(WebSocketEvent.OnConnectionLost)
                webSocketRef.set(null)
            }
        }

        // Store the listener so connect() can use it.
        listenerRef.set(listener)

        awaitClose {
            webSocketRef.getAndSet(null)?.cancel()
            _state.value = WebSocketState.Disconnected
        }
    }

    /**
     * Holds the current [WebSocketListener] created by [events] callbackFlow.
     */
    private val listenerRef = AtomicReference<WebSocketListener?>(null)

    override fun connect(url: String, headers: Map<String, String>) {
        if (_state.value is WebSocketState.Connected || _state.value is WebSocketState.Connecting) return

        _state.value = WebSocketState.Connecting
        val requestBuilder = Request.Builder().url(url)
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        val request = requestBuilder.build()

        val listener = listenerRef.get() ?: return
        val ws = webSocketFactory.create(okHttpClient, request, listener)
        webSocketRef.set(ws)
    }

    override fun send(message: WebSocketMessage): Boolean {
        val ws = webSocketRef.get() ?: return false
        return when (message) {
            is WebSocketMessage.Text -> ws.send(message.payload)
            is WebSocketMessage.Binary -> ws.send(message.payload)
        }
    }

    override fun close(code: Int, reason: String): Boolean {
        val ws = webSocketRef.get() ?: return false
        _state.value = WebSocketState.Disconnecting
        return ws.close(code, reason)
    }

    override fun cancel() {
        webSocketRef.getAndSet(null)?.cancel()
        _state.value = WebSocketState.Disconnected
    }
}
