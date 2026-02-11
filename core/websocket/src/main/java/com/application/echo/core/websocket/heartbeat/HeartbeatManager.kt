package com.application.echo.core.websocket.heartbeat

import kotlinx.coroutines.flow.StateFlow
import okio.ByteString

/**
 * Manages the ping/pong heartbeat mechanism for connection
 * health monitoring.
 *
 * When a pong is not received within the configured timeout,
 * [isAlive] becomes `false`, signalling the session layer to
 * trigger reconnection.
 */
interface HeartbeatManager {

    /**
     * Whether the connection is considered alive based on recent
     * pong responses. Becomes `false` when a pong timeout is exceeded.
     */
    val isAlive: StateFlow<Boolean>

    /**
     * Starts the periodic heartbeat.
     *
     * @param sendPing Suspend function that sends a ping frame.
     */
    fun start(sendPing: suspend (ByteString?) -> Unit)

    /** Stops the heartbeat ticker. */
    fun stop()

    /** Notifies the manager that a pong was received. */
    fun onPongReceived(payload: ByteString?)
}
