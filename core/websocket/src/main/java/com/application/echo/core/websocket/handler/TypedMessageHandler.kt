package com.application.echo.core.websocket.handler

/**
 * Handler for a specific WebSocket message type.
 *
 * Implementations declare the [messageType] they respond to and the
 * [MessageHandlerRegistry] routes matching messages to [handle].
 *
 * The "type" is extracted from a top-level `"type"` field in the
 * incoming JSON payload. For example, given:
 * ```json
 * { "type": "chat_message", "data": { … } }
 * ```
 * A handler with `messageType = "chat_message"` would receive the
 * raw JSON payload string.
 *
 * ```kotlin
 * class ChatMessageHandler @Inject constructor() : TypedMessageHandler {
 *     override val messageType = "chat_message"
 *     override suspend fun handle(payload: String) {
 *         val msg = gson.fromJson(payload, ChatMessage::class.java)
 *         // …
 *     }
 * }
 * ```
 */
interface TypedMessageHandler {

    /**
     * The message type key this handler responds to.
     *
     * Matched against the `"type"` field of incoming JSON messages.
     */
    val messageType: String

    /**
     * Called when a message matching [messageType] is received.
     *
     * @param payload The raw JSON payload string of the entire message.
     */
    suspend fun handle(payload: String)
}
