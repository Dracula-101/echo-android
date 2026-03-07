package com.application.echo.feature.auth.repository

import com.application.echo.core.api.auth.LoginResponse
import com.application.echo.core.api.auth.RegisterResponse
import com.application.echo.core.network.result.ApiResult
import com.application.echo.feature.auth.model.UserState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val userStateFlow: Flow<UserState>

    suspend fun login(email: String, password: String): ApiResult<LoginResponse>

    suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): ApiResult<RegisterResponse>

    fun logout()
}
