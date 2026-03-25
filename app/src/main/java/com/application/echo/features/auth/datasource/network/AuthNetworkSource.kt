package com.application.echo.features.auth.datasource.network

import com.application.echo.core.api.auth.AuthResult
import com.application.echo.core.api.auth.LoginResponse
import com.application.echo.core.api.auth.RefreshTokenResponse
import com.application.echo.core.api.auth.RegisterResponse

interface AuthNetworkSource {

    suspend fun login(email: String, password: String): AuthResult<LoginResponse>

    suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthResult<RegisterResponse>

    suspend fun refreshToken(refreshToken: String): AuthResult<RefreshTokenResponse>
}
