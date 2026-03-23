package com.application.echo.core.api.auth

import com.application.echo.core.network.result.toApiResult
import javax.inject.Inject

/**
 * Default [AuthApiRepository] backed by [AuthApiService].
 *
 * Converts raw [NetworkResponse] → [ApiResult] → [AuthResult],
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
    ): AuthLoginResult = api.login(
        request = LoginRequest(
            email = email,
            password = password,
            fcmToken = fcmToken,
            apnsToken = apnsToken,
        ),
    ).toApiResult().toAuthResult()

    override suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthRegisterResult = api.register(
        request = RegisterRequest(
            email = email,
            password = password,
            acceptTerms = acceptTerms,
        ),
    ).toApiResult().toAuthResult()

    override suspend fun refreshToken(
        refreshToken: String,
    ): AuthRefreshResult = api.refreshToken(
        request = RefreshTokenRequest(
            refreshToken = refreshToken,
        ),
    ).toApiResult().toAuthResult()
}
