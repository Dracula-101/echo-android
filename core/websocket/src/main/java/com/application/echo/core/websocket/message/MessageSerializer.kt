package com.application.echo.core.websocket.message

/**
 * Serializes typed objects into JSON strings for outbound WebSocket messages.
 */
interface MessageSerializer {

    /**
     * Serializes [data] of the given [type] to a JSON string.
     *
     * @throws com.application.echo.core.websocket.model.WebSocketException.SerializationError
     *         if serialization fails.
     */
    fun <T> serialize(data: T, type: Class<T>): String

    /**
     * Serializes [data] to a UTF-8 encoded byte array.
     *
     * @throws com.application.echo.core.websocket.model.WebSocketException.SerializationError
     *         if serialization fails.
     */
    fun serializeToBytes(data: Any): ByteArray
}
