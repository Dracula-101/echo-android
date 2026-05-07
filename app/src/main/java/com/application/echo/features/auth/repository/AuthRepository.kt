package com.application.echo.features.auth.repository

import com.application.echo.api.auth.AuthError
import com.application.echo.features.auth.model.AuthResult
import com.application.echo.api.auth.LoginResponse
import com.application.echo.api.auth.RegisterResponse
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

    suspend fun loginWithToken(token: String): AuthResult<LoginResponse>

    suspend fun silentLogin()

    suspend fun register(
        email: String,
        password: String,
        phoneNumber: String,
        phoneCountryCode: String,
        acceptTerms: Boolean,
    ): AuthResult<RegisterResponse>

    fun setPreRegistrationInfo(
        email: String? = null,
        password: String? = null,
    )

    fun logout()
}
