package com.application.echo.core.websocket.message

/**
 * Deserializes JSON payloads from inbound WebSocket messages into typed objects.
 */
interface MessageDeserializer {

    /**
     * Deserializes [payload] into an instance of [type].
     *
     * @throws com.application.echo.core.websocket.model.WebSocketException.SerializationError
     *         if deserialization fails.
     */
    fun <T> deserialize(payload: String, type: Class<T>): T

    /**
     * Attempts to deserialize [payload] into an instance of [type],
     * returning a [Result] instead of throwing.
     */
    fun <T> deserializeSafe(payload: String, type: Class<T>): Result<T>
}

/** Reified convenience for [MessageDeserializer.deserialize]. */
inline fun <reified T> MessageDeserializer.deserialize(payload: String): T =
    deserialize(payload, T::class.java)

/** Reified convenience for [MessageDeserializer.deserializeSafe]. */
inline fun <reified T> MessageDeserializer.deserializeSafe(payload: String): Result<T> =
    deserializeSafe(payload, T::class.java)
