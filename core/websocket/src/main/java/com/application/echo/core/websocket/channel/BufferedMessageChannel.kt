package com.application.echo.core.websocket.channel

import com.application.echo.core.websocket.model.WebSocketMessage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

/**
 * Default [MessageChannel] implementation with configurable buffering.
 *
 * - **Incoming:** backed by a [MutableSharedFlow] with `extraBufferCapacity`
 *   and [BufferOverflow.DROP_OLDEST] overflow strategy.
 * - **Outgoing:** backed by a [Channel.BUFFERED] channel.
 */
internal class BufferedMessageChannel @Inject constructor() : MessageChannel {

    private val _incoming = MutableSharedFlow<WebSocketMessage>(
        replay = 0,
        extraBufferCapacity = DEFAULT_BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val _outgoing = Channel<WebSocketMessage>(Channel.BUFFERED)

    override val incoming: Flow<WebSocketMessage> = _incoming.asSharedFlow()

    override val outgoing: SendChannel<WebSocketMessage> = _outgoing

    /** Emits a message to all active [incoming] collectors. */
    fun emitIncoming(message: WebSocketMessage) {
        _incoming.tryEmit(message)
    }

    /** Receives the next outgoing message (suspends until available). */
    suspend fun receiveOutgoing(): WebSocketMessage = _outgoing.receive()

    override fun close() {
        _outgoing.close()
    }

    private companion object {
        const val DEFAULT_BUFFER_CAPACITY = 64
    }
}
