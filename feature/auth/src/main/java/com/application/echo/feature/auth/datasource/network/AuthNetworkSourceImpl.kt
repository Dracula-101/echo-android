package com.application.echo.feature.auth.datasource.network

import com.application.echo.core.api.auth.AuthApiRepository
import com.application.echo.core.api.auth.AuthLoginResult
import com.application.echo.core.api.auth.AuthRefreshResult
import com.application.echo.core.api.auth.AuthRegisterResult
import javax.inject.Inject

class AuthNetworkSourceImpl @Inject constructor(
    private val api: AuthApiRepository,
) : AuthNetworkSource {

    override suspend fun login(
        email: String,
        password: String,
    ): AuthLoginResult = api.login(
        email = email,
        password = password,
    )

    override suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthRegisterResult = api.register(
        email = email,
        password = password,
        acceptTerms = acceptTerms,
    )

    override suspend fun refreshToken(
        refreshToken: String,
    ): AuthRefreshResult = api.refreshToken(
        refreshToken = refreshToken,
    )
}
