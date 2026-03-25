package com.application.echo.features.auth.datasource.network

import com.application.echo.core.api.auth.AuthApiRepository
import com.application.echo.core.api.auth.AuthResult
import com.application.echo.core.api.auth.LoginResponse
import com.application.echo.core.api.auth.RefreshTokenResponse
import com.application.echo.core.api.auth.RegisterResponse
import com.application.echo.core.api.auth.toAuthResult
import javax.inject.Inject

class AuthNetworkSourceImpl @Inject constructor(
    private val api: AuthApiRepository,
) : AuthNetworkSource {

    override suspend fun login(
        email: String,
        password: String,
    ): AuthResult<LoginResponse> = api.login(
        email = email,
        password = password,
    ).toAuthResult()

    override suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthResult<RegisterResponse> = api.register(
        email = email,
        password = password,
        acceptTerms = acceptTerms,
    ).toAuthResult()

    override suspend fun refreshToken(
        refreshToken: String,
    ): AuthResult<RefreshTokenResponse> = api.refreshToken(
        refreshToken = refreshToken,
    ).toAuthResult()
}
