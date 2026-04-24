package com.application.echo.core.network.model

/**
 * Static device metadata collected once at app startup.
 *
 * Sent as `X-Device-*` headers on every authenticated request so the
 * backend can identify the device for session management and analytics.
 */
data class DeviceInfo(
    val deviceId: String,
    val deviceName: String,
    val deviceType: String = "mobile",
    val platform: String = "Android",
    val osVersion: String,
    val model: String,
    val manufacturer: String
)
