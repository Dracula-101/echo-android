package com.application.echo.core.websocket.channel

import com.application.echo.core.websocket.model.WebSocketMessage
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow

/**
 * Bidirectional message channel with Flow-based receive and
 * channel-based send. Provides backpressure handling for both
 * directions.
 */
interface MessageChannel {

    /** Inbound messages as a hot [Flow] (replay = 0). */
    val incoming: Flow<WebSocketMessage>

    /** Outbound message sink. Send messages here to transmit them. */
    val outgoing: SendChannel<WebSocketMessage>

    /** Closes both the incoming and outgoing channels. */
    fun close()
}
