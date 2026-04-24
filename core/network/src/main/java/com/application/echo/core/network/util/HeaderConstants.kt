package com.application.echo.core.network.util

/**
 * Shared HTTP header name and value constants.
 */
object HeaderConstants {

    // ── Header Names ──
    const val AUTHORIZATION = "Authorization"
    const val ACCEPT = "Accept"
    const val CONTENT_TYPE = "Content-Type"
    const val X_PLATFORM = "X-Platform"
    const val X_APP_VERSION = "X-App-Version"
    const val X_REQUEST_ID = "X-Request-Id"

    // ── Header Values ──
    const val BEARER = "Bearer"
    const val APPLICATION_JSON = "application/json"
    const val ANDROID = "Android"

    const val DEVICE_ID = "X-Device-ID"
    const val DEVICE_NAME = "X-Device-Name"
    const val DEVICE_TYPE = "X-Device-Type"
    const val DEVICE_PLATFORM = "X-Device-Platform"
    const val DEVICE_OS = "X-Device-OS"
    const val DEVICE_OS_VERSION = "X-Device-OS-Version"
    const val DEVICE_MODEL = "X-Device-Model"
    const val DEVICE_MANUFACTURER = "X-Device-Manufacturer"


    const val SESSION_ID = "X-Session-ID"
    const val SESSION_TOKEN = "X-Session-Token"
}
