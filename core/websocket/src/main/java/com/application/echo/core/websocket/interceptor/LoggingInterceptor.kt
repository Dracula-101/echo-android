package com.application.echo.core.websocket.interceptor

import com.application.echo.core.websocket.config.WebSocketConfig
import com.application.echo.core.websocket.model.WebSocketMessage
import timber.log.Timber
import javax.inject.Inject

/**
 * Interceptor that logs all inbound and outbound WebSocket messages.
 *
 * When [WebSocketConfig.isDebug] is `true`, full payload content is
 * logged. In production builds only metadata (type + size) is logged.
 */
internal class LoggingInterceptor @Inject constructor(
    private val config: WebSocketConfig,
) : MessageInterceptor {

    override suspend fun interceptOutbound(message: WebSocketMessage): WebSocketMessage {
        when (message) {
            is WebSocketMessage.Text -> {
                if (config.isDebug) {
                    Timber.tag(TAG).d(">>> SEND text (%d chars): %s", message.payload.length, message.payload)
                } else {
                    Timber.tag(TAG).d(">>> SEND text (%d chars)", message.payload.length)
                }
            }

            is WebSocketMessage.Binary -> {
                Timber.tag(TAG).d(">>> SEND binary (%d bytes)", message.payload.size)
            }
        }
        return message
    }

    override suspend fun interceptInbound(message: WebSocketMessage): WebSocketMessage {
        when (message) {
            is WebSocketMessage.Text -> {
                if (config.isDebug) {
                    Timber.tag(TAG).d("<<< RECV text (%d chars): %s", message.payload.length, message.payload)
                } else {
                    Timber.tag(TAG).d("<<< RECV text (%d chars)", message.payload.length)
                }
            }

            is WebSocketMessage.Binary -> {
                Timber.tag(TAG).d("<<< RECV binary (%d bytes)", message.payload.size)
            }
        }
        return message
    }

    private companion object {
        const val TAG = "EchoWS"
    }
}
