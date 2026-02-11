package com.application.echo.core.websocket.model

/**
 * Type-safe WebSocket close codes as defined by RFC 6455.
 *
 * Eliminates magic numbers and provides semantic helpers for
 * categorising close reasons.
 */
@JvmInline
value class WebSocketCloseCode(val code: Int) {

    /** `true` when the code represents a normal, expected closure. */
    val isNormal: Boolean get() = code == NORMAL.code || code == GOING_AWAY.code

    /** `true` when the code indicates an error condition. */
    val isError: Boolean
        get() = code in setOf(
            PROTOCOL_ERROR.code,
            UNSUPPORTED_DATA.code,
            INVALID_PAYLOAD.code,
            POLICY_VIOLATION.code,
            MESSAGE_TOO_BIG.code,
            MANDATORY_EXTENSION.code,
            INTERNAL_ERROR.code,
        )

    /** `true` when the code suggests a transient failure worth retrying. */
    val isRetryable: Boolean
        get() = code in setOf(
            ABNORMAL.code,
            SERVICE_RESTART.code,
            TRY_AGAIN_LATER.code,
            INTERNAL_ERROR.code,
        )

    override fun toString(): String = "CloseCode($code)"

    companion object {
        // Standard RFC 6455 close codes
        val NORMAL = WebSocketCloseCode(1000)
        val GOING_AWAY = WebSocketCloseCode(1001)
        val PROTOCOL_ERROR = WebSocketCloseCode(1002)
        val UNSUPPORTED_DATA = WebSocketCloseCode(1003)
        val NO_STATUS_RECEIVED = WebSocketCloseCode(1005)
        val ABNORMAL = WebSocketCloseCode(1006)
        val INVALID_PAYLOAD = WebSocketCloseCode(1007)
        val POLICY_VIOLATION = WebSocketCloseCode(1008)
        val MESSAGE_TOO_BIG = WebSocketCloseCode(1009)
        val MANDATORY_EXTENSION = WebSocketCloseCode(1010)
        val INTERNAL_ERROR = WebSocketCloseCode(1011)
        val SERVICE_RESTART = WebSocketCloseCode(1012)
        val TRY_AGAIN_LATER = WebSocketCloseCode(1013)
    }
}
