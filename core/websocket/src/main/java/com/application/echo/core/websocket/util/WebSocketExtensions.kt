package com.application.echo.core.websocket.util

import com.application.echo.core.websocket.model.WebSocketEvent
import com.application.echo.core.websocket.model.WebSocketException
import com.application.echo.core.websocket.model.WebSocketMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import okio.ByteString

// ──────────────── Event Flow Extensions ────────────────

/**
 * Extracts only [WebSocketMessage]s from a stream of [WebSocketEvent]s.
 *
 * ```kotlin
 * session.events.filterMessages().collect { message -> … }
 * ```
 */
fun Flow<WebSocketEvent>.filterMessages(): Flow<WebSocketMessage> =
    filterIsInstance<WebSocketEvent.OnMessage>().map { it.message }

/**
 * Extracts only [WebSocketException]s from a stream of [WebSocketEvent]s.
 *
 * ```kotlin
 * session.events.filterErrors().collect { error -> … }
 * ```
 */
fun Flow<WebSocketEvent>.filterErrors(): Flow<WebSocketException> =
    filterIsInstance<WebSocketEvent.OnError>().map { it.exception }

/**
 * Emits only [WebSocketEvent.OnConnected] events from the event stream.
 */
fun Flow<WebSocketEvent>.filterConnected(): Flow<String> =
    filterIsInstance<WebSocketEvent.OnConnected>().map { it.url }

/**
 * Emits only [WebSocketEvent.OnDisconnected] events from the event stream.
 */
fun Flow<WebSocketEvent>.filterDisconnected(): Flow<Pair<Int, String>> =
    filterIsInstance<WebSocketEvent.OnDisconnected>().map { it.code to it.reason }

// ──────────────── Message Flow Extensions ────────────────

/**
 * Filters to text messages only, emitting their string payloads.
 *
 * ```kotlin
 * session.messages.filterText().collect { json -> … }
 * ```
 */
fun Flow<WebSocketMessage>.filterText(): Flow<String> =
    filterIsInstance<WebSocketMessage.Text>().map { it.payload }

/**
 * Filters to binary messages only, emitting their [ByteString] payloads.
 */
fun Flow<WebSocketMessage>.filterBinary(): Flow<ByteString> =
    filterIsInstance<WebSocketMessage.Binary>().map { it.payload }

/**
 * Deserializes text messages into typed objects using [gson].
 *
 * Messages that fail to deserialize are silently dropped.
 *
 * ```kotlin
 * session.messages.deserialize<ChatMessage>(gson).collect { msg -> … }
 * ```
 */
inline fun <reified T> Flow<WebSocketMessage>.deserialize(gson: Gson): Flow<T> =
    filterIsInstance<WebSocketMessage.Text>().mapNotNull { message ->
        try {
            gson.fromJson(message.payload, T::class.java)
        } catch (_: Exception) {
            null
        }
    }

/**
 * Filters text messages by a `"type"` field value in the JSON payload.
 *
 * ```kotlin
 * session.messages.filterByType("chat_message").collect { json -> … }
 * ```
 */
fun Flow<WebSocketMessage>.filterByType(type: String): Flow<String> =
    filterIsInstance<WebSocketMessage.Text>().mapNotNull { message ->
        try {
            val json = JsonParser.parseString(message.payload).asJsonObject
            if (json.get("type")?.asString == type) message.payload else null
        } catch (_: Exception) {
            null
        }
    }

// ──────────────── Message Parsing Extensions ────────────────

/**
 * Attempts to parse a text message payload as a [JsonObject].
 *
 * @return The parsed object, or `null` if the payload is not valid JSON.
 */
fun WebSocketMessage.Text.toJsonObject(): JsonObject? {
    return try {
        JsonParser.parseString(payload).asJsonObject
    } catch (_: Exception) {
        null
    }
}

/**
 * Extracts the `"type"` field from a text message's JSON payload.
 *
 * @return The type string, or `null` if not present or not valid JSON.
 */
fun WebSocketMessage.Text.extractType(): String? {
    return toJsonObject()?.get("type")?.asString
}

/**
 * Extracts the `"data"` field from a text message's JSON payload as a string.
 *
 * @return The data sub-JSON, or `null` if not present.
 */
fun WebSocketMessage.Text.extractData(): String? {
    return toJsonObject()?.get("data")?.toString()
}
