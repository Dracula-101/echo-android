package com.application.echo.core.api.session

/**
 * Contract for supplying session and device metadata to the API layer.
 *
 * Implement this in your app module and bind it via Hilt.
 * The [SessionHeaderInterceptor] reads from this provider to attach
 * `X-Session-ID`, `X-Session-Token`, and `X-Device-*` headers
 * to every authenticated request.
 *
 * ```kotlin
 * @Singleton
 * class EchoSessionProvider @Inject constructor(
 *     private val prefs: SessionPreferences,
 * ) : SessionProvider {
 *     override val sessionId get() = prefs.sessionId
 *     override val sessionToken get() = prefs.sessionToken
 *     override val deviceInfo get() = DeviceInfo(...)
 * }
 * ```
 */
interface SessionProvider {

    /** Current session ID, or `null` if not authenticated. */
    val sessionId: String?

    /** Current session token, or `null` if not authenticated. */
    val sessionToken: String?

    /** Static device metadata attached to every request. */
    val deviceInfo: DeviceInfo
}

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
    val osVersion: String = android.os.Build.VERSION.RELEASE,
    val model: String = android.os.Build.MODEL,
    val manufacturer: String = android.os.Build.MANUFACTURER,
)
