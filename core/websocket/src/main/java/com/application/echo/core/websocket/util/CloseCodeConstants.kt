package com.application.echo.core.websocket.util

import com.application.echo.core.websocket.model.WebSocketCloseCode

/**
 * Integer constants for all standard RFC 6455 WebSocket close codes.
 *
 * Use these when you need a raw `Int` instead of the [WebSocketCloseCode]
 * value class — for example, when calling [WebSocket.close(code, reason)].
 */
object CloseCodeConstants {

    /** 1000 — Normal closure. */
    const val NORMAL_CLOSURE = 1000

    /** 1001 — Endpoint is going away (server shutdown, browser navigation, etc.). */
    const val GOING_AWAY = 1001

    /** 1002 — Termination due to a protocol error. */
    const val PROTOCOL_ERROR = 1002

    /** 1003 — Received unsupported data type. */
    const val UNSUPPORTED_DATA = 1003

    /** 1005 — No status code was present (must not be sent by endpoints). */
    const val NO_STATUS_RECEIVED = 1005

    /** 1006 — Connection closed abnormally without a close frame. */
    const val ABNORMAL_CLOSURE = 1006

    /** 1007 — Inconsistent data within a message (e.g. non-UTF-8 text). */
    const val INVALID_PAYLOAD = 1007

    /** 1008 — Message violates the endpoint's policy. */
    const val POLICY_VIOLATION = 1008

    /** 1009 — Message too large for the endpoint to process. */
    const val MESSAGE_TOO_BIG = 1009

    /** 1010 — Client expected server to negotiate an extension. */
    const val MANDATORY_EXTENSION = 1010

    /** 1011 — Server encountered an unexpected condition. */
    const val INTERNAL_ERROR = 1011

    /** 1012 — Server is restarting. */
    const val SERVICE_RESTART = 1012

    /** 1013 — Server temporarily unable — try again later. */
    const val TRY_AGAIN_LATER = 1013

    /** 1014 — Bad gateway (proxy received invalid upstream response). */
    const val BAD_GATEWAY = 1014

    /** 1015 — TLS handshake failure (must not be sent by endpoints). */
    const val TLS_HANDSHAKE = 1015
}

// ──────────────── Extensions ────────────────

/** Converts a raw close code integer into a [WebSocketCloseCode] value class. */
fun Int.toWebSocketCloseCode(): WebSocketCloseCode = WebSocketCloseCode(this)

/** `true` when this integer represents a normal (1000) close. */
fun Int.isNormalClosure(): Boolean = this == CloseCodeConstants.NORMAL_CLOSURE

/** `true` when this integer represents a retryable close code. */
fun Int.isRetryableCloseCode(): Boolean = WebSocketCloseCode(this).isRetryable
