package com.application.echo.features.auth.repository

import com.application.echo.features.auth.model.AuthResult
import com.application.echo.core.api.auth.LoginResponse
import com.application.echo.core.api.auth.RegisterResponse
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.model.UserState
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    /**
     * Combined auth lifecycle state.
     *
     * Emits [AuthState.Initializing] on cold start while the session
     * is being restored / token is being refreshed, then settles to
     * [AuthState.Authenticated] or [AuthState.Unauthenticated].
     */
    val authStateFlow: StateFlow<AuthState>

    /** Raw user data. Empty when not logged in. */
    val userStateFlow: StateFlow<UserState>

    suspend fun login(email: String, password: String): AuthResult<LoginResponse>

    suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthResult<RegisterResponse>

    fun logout()
}
