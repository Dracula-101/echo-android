package com.application.echo.core.websocket.model

import okio.ByteString

/**
 * A WebSocket message — either text (UTF-8) or binary.
 */
sealed class WebSocketMessage {

    /**
     * A UTF-8 text frame.
     *
     * @property payload The text content of the message.
     */
    data class Text(val payload: String) : WebSocketMessage()

    /**
     * A binary frame.
     *
     * @property payload The binary content of the message.
     */
    data class Binary(val payload: ByteString) : WebSocketMessage()
}

/** Returns the text payload or `null` if this is a [WebSocketMessage.Binary]. */
fun WebSocketMessage.asText(): String? = (this as? WebSocketMessage.Text)?.payload

/** Returns the binary payload or `null` if this is a [WebSocketMessage.Text]. */
fun WebSocketMessage.asBinary(): ByteString? = (this as? WebSocketMessage.Binary)?.payload
