package com.application.echo.features.auth.datasource.network

import com.application.echo.features.auth.model.AuthResult
import com.application.echo.api.auth.LoginResponse
import com.application.echo.api.auth.RefreshTokenResponse
import com.application.echo.api.auth.RegisterResponse

interface AuthNetworkSource {

    suspend fun login(email: String, password: String): AuthResult<LoginResponse>

    suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthResult<RegisterResponse>

    suspend fun refreshToken(refreshToken: String): AuthResult<RefreshTokenResponse>
}
