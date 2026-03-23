package com.application.echo.feature.auth.repository

import com.application.echo.core.api.auth.AuthLoginResult
import com.application.echo.core.api.auth.AuthRegisterResult
import com.application.echo.feature.auth.model.UserState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val userStateFlow: Flow<UserState>

    suspend fun login(email: String, password: String): AuthLoginResult

    suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthRegisterResult

    fun logout()
}
