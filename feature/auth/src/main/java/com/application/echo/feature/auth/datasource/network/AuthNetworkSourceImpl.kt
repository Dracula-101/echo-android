package com.application.echo.feature.auth.datasource.network

import com.application.echo.core.api.auth.AuthApiRepository
import com.application.echo.core.api.auth.LoginResponse
import com.application.echo.core.api.auth.RegisterResponse
import com.application.echo.core.network.result.ApiResult
import javax.inject.Inject

class AuthNetworkSourceImpl @Inject constructor(
    private val api: AuthApiRepository,
) : AuthNetworkSource {

    override suspend fun login(
        email: String,
        password: String,
    ): ApiResult<LoginResponse> = api.login(
        email = email,
        password = password,
    )

    override suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): ApiResult<RegisterResponse> = api.register(
        email = email,
        password = password,
        acceptTerms = acceptTerms,
    )
}
