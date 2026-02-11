package com.application.echo.core.websocket.channel

import com.application.echo.core.websocket.model.WebSocketMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject

/**
 * Offline message buffer that queues outbound messages while the
 * WebSocket is disconnected and flushes them upon reconnection.
 *
 * Thread-safe: backed by a [ConcurrentLinkedQueue].
 *
 * @property maxSize Maximum number of buffered messages. When exceeded,
 *           the oldest messages are discarded.
 */
internal class MessageBuffer @Inject constructor() {

    private val queue = ConcurrentLinkedQueue<WebSocketMessage>()
    private val _pendingCount = MutableStateFlow(0)

    /** Observable count of buffered messages. */
    val pendingCount: StateFlow<Int> = _pendingCount.asStateFlow()

    /** Maximum buffer size. Oldest messages are dropped when exceeded. */
    var maxSize: Int = DEFAULT_MAX_SIZE

    /**
     * Adds a message to the buffer. If the buffer is full, the oldest
     * message is removed to make room.
     */
    fun enqueue(message: WebSocketMessage) {
        while (queue.size >= maxSize) {
            queue.poll()
        }
        queue.offer(message)
        _pendingCount.value = queue.size
    }

    /**
     * Removes and returns all buffered messages in FIFO order.
     * The buffer is empty after this call.
     */
    fun drain(): List<WebSocketMessage> {
        val messages = mutableListOf<WebSocketMessage>()
        while (true) {
            val msg = queue.poll() ?: break
            messages.add(msg)
        }
        _pendingCount.value = 0
        return messages
    }

    /** Discards all buffered messages. */
    fun clear() {
        queue.clear()
        _pendingCount.value = 0
    }

    private companion object {
        const val DEFAULT_MAX_SIZE = 100
    }
}
