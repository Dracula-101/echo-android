package com.application.echo.feature.auth.datasource.network

import com.application.echo.core.api.auth.LoginResponse
import com.application.echo.core.api.auth.RegisterResponse
import com.application.echo.core.network.result.ApiResult

interface AuthNetworkSource {

    suspend fun login(email: String, password: String): ApiResult<LoginResponse>

    suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): ApiResult<RegisterResponse>
}
