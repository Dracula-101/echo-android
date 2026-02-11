package com.application.echo.core.api.auth

import com.application.echo.core.network.result.ApiResult
import com.application.echo.core.network.result.toApiResult
import javax.inject.Inject

/**
 * Default [AuthRepository] backed by [AuthApiService].
 */
internal class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
) : AuthRepository {

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
    ): ApiResult<RefreshTokenResponse> = api.refreshToken(
        request = RefreshTokenRequest(
            refreshToken = refreshToken,
        ),
    ).toApiResult()
}
