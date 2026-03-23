package com.application.echo.feature.auth.repository

import com.application.echo.core.api.auth.AuthLoginResult
import com.application.echo.core.api.auth.AuthRegisterResult
import com.application.echo.feature.auth.model.AuthState
import com.application.echo.feature.auth.model.UserState
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

    suspend fun login(email: String, password: String): AuthLoginResult

    suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthRegisterResult

    fun logout()
}
