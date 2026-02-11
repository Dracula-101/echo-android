package com.application.echo.core.websocket.message

import com.application.echo.core.websocket.model.WebSocketException
import com.google.gson.Gson
import javax.inject.Inject

/**
 * Gson-backed [MessageSerializer] implementation.
 *
 * Wraps serialization failures in [WebSocketException.SerializationError]
 * so callers only need to handle domain-level errors.
 */
internal class GsonMessageSerializer @Inject constructor(
    private val gson: Gson,
) : MessageSerializer {

    override fun <T> serialize(data: T, type: Class<T>): String {
        return try {
            gson.toJson(data, type)
        } catch (e: Exception) {
            throw WebSocketException.SerializationError(e).throwable
        }
    }

    override fun serializeToBytes(data: Any): ByteArray {
        return try {
            gson.toJson(data).toByteArray(Charsets.UTF_8)
        } catch (e: Exception) {
            throw WebSocketException.SerializationError(e).throwable
        }
    }
}
