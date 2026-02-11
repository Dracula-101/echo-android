package com.application.echo.core.websocket.handler

import com.application.echo.core.websocket.model.WebSocketMessage
import com.google.gson.JsonParser
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Routes incoming [WebSocketMessage.Text] messages to registered
 * [TypedMessageHandler]s based on a `"type"` field in the JSON payload.
 *
 * Thread-safe: handlers are stored in a [ConcurrentHashMap].
 *
 * ```kotlin
 * registry.register(chatMessageHandler)
 * registry.register(presenceHandler)
 *
 * // Later, in an event collector:
 * session.messages.collect { message ->
 *     registry.dispatch(message)
 * }
 * ```
 */
@Singleton
class MessageHandlerRegistry @Inject constructor() {

    private val handlers = ConcurrentHashMap<String, TypedMessageHandler>()

    /**
     * Fallback handler invoked when no registered handler matches the
     * message type. Defaults to a no-op that logs a warning.
     */
    var fallbackHandler: (suspend (type: String?, payload: String) -> Unit)? = null

    /** Registers a [handler] for its declared [TypedMessageHandler.messageType]. */
    fun register(handler: TypedMessageHandler) {
        handlers[handler.messageType] = handler
        Timber.tag(TAG).d("Registered handler for type: %s", handler.messageType)
    }

    /** Removes the handler for [type], if any. */
    fun unregister(type: String) {
        handlers.remove(type)
        Timber.tag(TAG).d("Unregistered handler for type: %s", type)
    }

    /** Returns all currently registered message types. */
    fun registeredTypes(): Set<String> = handlers.keys.toSet()

    /**
     * Dispatches a [message] to the appropriate handler.
     *
     * Binary messages are silently ignored. Text messages are parsed as
     * JSON and routed by the `"type"` field. If no handler is found, the
     * [fallbackHandler] is invoked (if set).
     */
    suspend fun dispatch(message: WebSocketMessage) {
        if (message !is WebSocketMessage.Text) return

        val payload = message.payload
        val type = extractType(payload)

        if (type == null) {
            Timber.tag(TAG).w("No \"type\" field found in message — invoking fallback")
            fallbackHandler?.invoke(null, payload)
            return
        }

        val handler = handlers[type]
        if (handler != null) {
            try {
                handler.handle(payload)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Handler for type \"%s\" threw an exception", type)
            }
        } else {
            Timber.tag(TAG).d("No handler registered for type: %s", type)
            fallbackHandler?.invoke(type, payload)
        }
    }

    // ──────────────── Helpers ────────────────

    /**
     * Extracts the `"type"` field from a JSON string.
     *
     * Returns `null` if the payload is not valid JSON or lacks a `"type"` field.
     */
    private fun extractType(payload: String): String? {
        return try {
            val jsonObject = JsonParser.parseString(payload).asJsonObject
            jsonObject.get(TYPE_FIELD)?.asString
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val TAG = "EchoWS"
        const val TYPE_FIELD = "type"
    }
}
