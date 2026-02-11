package com.application.echo.core.websocket.session

import com.application.echo.core.websocket.config.WebSocketConfig
import com.application.echo.core.websocket.model.WebSocketState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Manages multiple [WebSocketSession]s keyed by a caller-defined identifier.
 *
 * Useful when the app maintains connections to more than one WebSocket
 * endpoint simultaneously (e.g., chat + notifications).
 *
 * Thread-safe: all lookups and mutations go through a [ConcurrentHashMap].
 */
@Singleton
class WebSocketSessionManager @Inject constructor(
    private val sessionProvider: Provider<WebSocketSession>,
) {

    private val sessions = ConcurrentHashMap<String, WebSocketSession>()
    private val _sessionKeys = MutableStateFlow<Set<String>>(emptySet())

    /**
     * Returns the session for [key], creating a new one with [config] if
     * it does not already exist.
     */
    fun getOrCreate(key: String, config: WebSocketConfig): WebSocketSession {
        return sessions.getOrPut(key) {
            sessionProvider.get().also {
                _sessionKeys.value = sessions.keys.toSet() + key
            }
        }
    }

    /** Returns an existing session for [key], or `null`. */
    fun get(key: String): WebSocketSession? = sessions[key]

    /** Disconnects and removes the session for [key]. */
    fun close(key: String) {
        sessions.remove(key)?.disconnect()
        _sessionKeys.value = sessions.keys.toSet()
    }

    /** Disconnects and removes all active sessions. */
    fun closeAll() {
        sessions.values.forEach { it.disconnect() }
        sessions.clear()
        _sessionKeys.value = emptySet()
    }

    /** Observable map of active session keys → their current states. */
    val activeSessions: Flow<Map<String, WebSocketState>>
        get() = _sessionKeys.asStateFlow().map { keys ->
            keys.associateWith { key -> sessions[key]?.state?.value ?: WebSocketState.Disconnected }
        }
}
