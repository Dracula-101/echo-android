package com.application.echo.core.network.provider

/**
 * Contract for supplying session and device metadata to the API layer.
 *
 * Implement this in your app module and bind it via Hilt.
 * The [SessionProvider] reads from this provider to attach
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
 * }
 * ```
 */
interface SessionProvider {

    /** Current session ID, or `null` if not authenticated. */
    val sessionId: String?

    /** Current session token, or `null` if not authenticated. */
    val sessionToken: String?
}