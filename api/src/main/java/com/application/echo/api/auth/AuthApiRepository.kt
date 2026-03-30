package com.application.echo.api.auth

import com.application.echo.core.network.result.ApiResult

/**
 * Public contract for all authentication operations.
 *
 * All methods return [com.application.echo.features.auth.model.AuthResult] — a sealed interface that is either
 * [com.application.echo.features.auth.model.AuthResult.Success] carrying the response DTO, or [com.application.echo.features.auth.model.AuthResult.Error]
 * carrying a typed [AuthError]. No Retrofit, OkHttp, or generic
 * network types leak into the consuming layer.
 */
interface AuthApiRepository {

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
