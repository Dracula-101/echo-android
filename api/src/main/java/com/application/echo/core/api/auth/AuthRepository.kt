package com.application.echo.core.api.auth

import com.application.echo.core.network.result.ApiResult

/**
 * Public contract for all authentication operations.
 *
 * All methods return [ApiResult] — a sealed interface that is either
 * [ApiResult.Success] or [ApiResult.Failure]. No Retrofit or OkHttp
 * types leak into the consuming layer.
 */
interface AuthRepository {

    /**
     * Authenticate with email + password.
     *
     * On success, returns [LoginResponse] containing the user
     * and session credentials (tokens, session ID).
     */
    suspend fun login(
        email: String,
        password: String,
        fcmToken: String? = null,
        apnsToken: String? = null,
    ): ApiResult<LoginResponse>

    /**
     * Register a new account.
     *
     * On success, returns [RegisterResponse] containing the new
     * user and session credentials.
     */
    suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): ApiResult<RegisterResponse>

    /**
     * Exchange a refresh token for a new access + refresh token pair.
     */
    suspend fun refreshToken(
        refreshToken: String,
    ): ApiResult<RefreshTokenResponse>
}
