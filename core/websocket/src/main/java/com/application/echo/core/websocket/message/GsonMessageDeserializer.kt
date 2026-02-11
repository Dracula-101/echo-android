package com.application.echo.core.websocket.message

import com.application.echo.core.websocket.model.WebSocketException
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import javax.inject.Inject

/**
 * Gson-backed [MessageDeserializer] implementation.
 *
 * Wraps [JsonSyntaxException] and other parse errors into
 * [WebSocketException.SerializationError].
 */
internal class GsonMessageDeserializer @Inject constructor(
    private val gson: Gson,
) : MessageDeserializer {

    override fun <T> deserialize(payload: String, type: Class<T>): T {
        return try {
            gson.fromJson(payload, type)
        } catch (e: JsonSyntaxException) {
            throw WebSocketException.SerializationError(e, payload).throwable
        } catch (e: Exception) {
            throw WebSocketException.SerializationError(e, payload).throwable
        }
    }

    override fun <T> deserializeSafe(payload: String, type: Class<T>): Result<T> {
        return try {
            Result.success(gson.fromJson(payload, type))
        } catch (e: JsonSyntaxException) {
            Result.failure(WebSocketException.SerializationError(e, payload).throwable)
        } catch (e: Exception) {
            Result.failure(WebSocketException.SerializationError(e, payload).throwable)
        }
    }
}
