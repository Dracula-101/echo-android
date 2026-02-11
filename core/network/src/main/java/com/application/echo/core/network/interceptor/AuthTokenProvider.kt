package com.application.echo.core.network.interceptor

/**
 * Contract for supplying the current authentication token.
 *
 * Implement this in the data layer and bind it via Hilt so the
 * [AuthInterceptor] can attach the Bearer token automatically.
 *
 * Return `null` when no token is available (e.g. the user is logged out).
 */
interface AuthTokenProvider {

    /**
     * Returns the latest valid auth token, or `null` if not authenticated.
     */
    fun getLatestAuthToken(): String?
}
