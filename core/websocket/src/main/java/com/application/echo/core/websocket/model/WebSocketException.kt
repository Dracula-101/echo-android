package com.application.echo.core.websocket.model

/**
 * Comprehensive error hierarchy for WebSocket operations.
 *
 * Every failure that can occur during a WebSocket session is captured
 * as a sealed subtype — callers never need to catch raw exceptions.
 */
sealed class WebSocketException {

    /** The underlying [Throwable] that caused the error. */
    abstract val throwable: Throwable

    // ──────────────── Connection ────────────────

    /**
     * The initial WebSocket connection could not be established.
     *
     * @property url The endpoint that was unreachable.
     */
    data class ConnectionFailed(
        override val throwable: Throwable,
        val url: String,
    ) : WebSocketException() {
        val message: String get() = throwable.message ?: "Failed to connect to $url"
    }

    // ──────────────── Messaging ────────────────

    /**
     * A message could not be sent over the WebSocket.
     */
    data class MessageSendFailed(
        override val throwable: Throwable,
        val message: WebSocketMessage,
    ) : WebSocketException() {
        val errorMessage: String get() = throwable.message ?: "Failed to send message"
    }

    // ──────────────── Protocol ────────────────

    /**
     * A WebSocket protocol-level error (unexpected close code, frame error, etc.).
     */
    data class ProtocolError(
        override val throwable: Throwable,
        val code: Int,
        val reason: String,
    ) : WebSocketException() {
        val message: String get() = "Protocol error $code: $reason"
    }

    // ──────────────── Timeout ────────────────

    /**
     * A connection or read/write operation timed out.
     */
    data class Timeout(override val throwable: Throwable) : WebSocketException() {
        val message: String get() = "WebSocket operation timed out"
    }

    // ──────────────── Serialization ────────────────

    /**
     * A message payload could not be serialized or deserialized.
     */
    data class SerializationError(
        override val throwable: Throwable,
        val rawPayload: String? = null,
    ) : WebSocketException() {
        val message: String get() = throwable.message ?: "Serialization error"
    }

    // ──────────────── Closed ────────────────

    /**
     * The WebSocket was closed (may or may not be an error depending on the code).
     */
    data class Closed(
        val code: Int,
        val reason: String,
    ) : WebSocketException() {
        override val throwable: Throwable get() = IllegalStateException("WebSocket closed: $code $reason")
        val message: String get() = "WebSocket closed ($code): $reason"

        /** `true` when the close code indicates a normal shutdown. */
        val isNormal: Boolean get() = code == WebSocketCloseCode.NORMAL.code
    }

    // ──────────────── Unknown ────────────────

    /**
     * Catch-all for unexpected errors.
     */
    data class Unknown(override val throwable: Throwable) : WebSocketException() {
        val message: String get() = throwable.message ?: "An unexpected WebSocket error occurred"
    }
}
