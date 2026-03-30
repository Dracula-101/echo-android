package com.application.echo.api.auth

import com.application.echo.core.network.result.ApiResult
import com.application.echo.core.network.result.toApiResult
import javax.inject.Inject

/**
 * Default [AuthApiRepository] backed by [AuthApiService].
 *
 * Converts raw [NetworkResponse] → [ApiResult] → [com.application.echo.features.auth.model.AuthResult],
 * mapping network-level errors to typed [AuthError] subtypes.
 */
internal class AuthApiRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
) : AuthApiRepository {

    override suspend fun login(
        email: String,
        password: String,
        fcmToken: String?,
        apnsToken: String?,
    ): ApiResult<LoginResponse> = api.login(
        request = LoginRequest(
            email = email,
            password = password,
            fcmToken = fcmToken,
            apnsToken = apnsToken,
        ),
    ).toApiResult()

    override suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): ApiResult<RegisterResponse> = api.register(
        request = RegisterRequest(
            email = email,
            password = password,
            acceptTerms = acceptTerms,
        ),
    ).toApiResult()

    override suspend fun refreshToken(
        refreshToken: String,
    ): ApiResult<RefreshTokenResponse>  = api.refreshToken(
        request = RefreshTokenRequest(
            refreshToken = refreshToken,
        ),
    ).toApiResult()
}
