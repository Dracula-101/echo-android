package com.application.echo.core.websocket.connection

import com.application.echo.core.websocket.qualifier.WebSocketOkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

/**
 * Factory that creates OkHttp [WebSocket] instances.
 *
 * Exists as a thin abstraction layer to make [OkHttpWebSocketConnection]
 * easily testable — tests can substitute a fake factory without needing
 * a real OkHttp client.
 */
internal class WebSocketFactory @Inject constructor() {

    /**
     * Creates and starts a new WebSocket connection.
     */
    fun create(
        client: OkHttpClient,
        request: Request,
        listener: WebSocketListener,
    ): WebSocket = client.newWebSocket(request, listener)
}
