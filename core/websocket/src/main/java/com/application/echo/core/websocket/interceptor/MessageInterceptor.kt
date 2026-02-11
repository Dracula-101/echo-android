package com.application.echo.core.websocket.interceptor

import com.application.echo.core.websocket.model.WebSocketMessage

/**
 * Intercepts outbound and inbound WebSocket messages.
 *
 * Implementations can transform, enrich, or log messages as they pass
 * through the interceptor chain. The default implementation is a
 * pass-through (no-op).
 *
 * Register interceptors via Hilt multi-binding:
 * ```kotlin
 * @Binds @IntoSet
 * fun bindLogging(impl: LoggingInterceptor): MessageInterceptor
 * ```
 */
interface MessageInterceptor {

    /**
     * Called before a message is sent to the server.
     * Return the (possibly modified) message to continue the chain.
     */
    suspend fun interceptOutbound(message: WebSocketMessage): WebSocketMessage = message

    /**
     * Called when a message is received from the server.
     * Return the (possibly modified) message to continue the chain.
     */
    suspend fun interceptInbound(message: WebSocketMessage): WebSocketMessage = message
}
