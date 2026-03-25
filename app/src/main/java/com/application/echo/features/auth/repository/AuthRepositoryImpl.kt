package com.application.echo.features.auth.repository

import com.application.echo.core.api.auth.AuthError
import com.application.echo.core.api.auth.AuthResult
import com.application.echo.core.api.auth.LoginResponse
import com.application.echo.core.api.auth.RegisterResponse
import com.application.echo.core.api.auth.SessionInfo
import com.application.echo.core.api.auth.fold
import com.application.echo.core.api.auth.onError
import com.application.echo.core.api.auth.onSuccess
import com.application.echo.core.api.manager.AuthTokenManager
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.features.auth.datasource.disk.AuthDiskSource
import com.application.echo.features.auth.datasource.network.AuthNetworkSource
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.model.UserState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

private const val DEFAULT_TOKEN_LIFETIME_SECONDS = 86_400L // 24 hours

class AuthRepositoryImpl @Inject constructor(
    private val networkSource: AuthNetworkSource,
    private val diskSource: AuthDiskSource,
    private val tokenManager: AuthTokenManager,
    @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    /** Guards all session-mutating operations (login, register, refresh, logout). */
    private val sessionMutex = Mutex()

    private val _authStateFlow = MutableStateFlow<AuthState>(AuthState.Initializing)
    override val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    override val userStateFlow: StateFlow<UserState> =
        MutableStateFlow(diskSource.userState)

    init {
        scope.launch { restoreSession() }
    }

    // ── Public API ───────────────────────────────────────────────────

    override suspend fun login(
        email: String,
        password: String,
    ): AuthResult<LoginResponse> = sessionMutex.withLock {
        val result = networkSource.login(email, password)
        result.onSuccess { response ->
            persistSession(response.session, response.user.id, response.user.email)
            emitAuthenticated()
        }
        result
    }

    override suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthResult<RegisterResponse> = sessionMutex.withLock {
        val registerResult = networkSource.register(email, password, acceptTerms)

        return registerResult.fold(
            onSuccess = { response ->
                if (response.requiresEmailVerification) {
                    _authStateFlow.value = AuthState.Unauthenticated(
                        AuthState.Unauthenticated.Reason.EmailVerificationRequired
                    )
                    return@fold registerResult
                }

                val loginResult = networkSource.login(email, password)
                loginResult.fold(
                    onSuccess = { loginResponse ->
                        persistSession(
                            loginResponse.session,
                            loginResponse.user.id,
                            loginResponse.user.email
                        )
                        emitAuthenticated()
                        registerResult
                    },
                    onError = { error ->
                        clearSession()
                        return@fold AuthResult.Error(error)
                    }
                )
            },
            onError = { error ->
                AuthResult.Error(error)
            }
        )
    }

    override fun logout() {
        clearSession()
        _authStateFlow.value = AuthState.Unauthenticated(AuthState.Unauthenticated.Reason.LoggedOut)
    }

    // ── Session Restoration ──────────────────────────────────────────

    /**
     * Called once at init. Checks for a persisted session and tries to
     * ensure we have a valid access token before declaring auth state.
     */
    private suspend fun restoreSession() = sessionMutex.withLock {
        val storedUser = diskSource.userState
        if (storedUser == UserState.Companion.Empty) {
            Timber.d("No stored session — unauthenticated")
            _authStateFlow.value = AuthState.Unauthenticated()
            return
        }

        // Token still valid — go straight to authenticated.
        if (tokenManager.isTokenValid) {
            Timber.d("Stored session with valid token — authenticated")
            emitAuthenticated()
            return
        }

        // Token expired — try refreshing.
        val tokenData = tokenManager.getLatestAuthTokenData()
        if (tokenData == null) {
            Timber.w("Stored user but no token data — clearing stale session")
            clearSession()
            _authStateFlow.value = AuthState.Unauthenticated(AuthState.Unauthenticated.Reason.SessionExpired)
            return
        }

        Timber.d("Access token expired — attempting refresh")
        networkSource.refreshToken(tokenData.refreshToken).fold(
            onSuccess = { response ->
                storeTokens(response.accessToken, response.refreshToken)
                Timber.d("Token refreshed — authenticated")
                emitAuthenticated()
            },
            onError = { error ->
                handleRefreshError(error)
            },
        )
    }

    // ── Refresh Error Handling ────────────────────────────────────────

    /**
     * Only logs out on terminal auth errors. Transient failures (network,
     * timeout, server errors) leave the session intact so the next API
     * call or app foreground can retry.
     */
    private fun handleRefreshError(error: AuthError) {
        when (error) {
            // Terminal — the refresh token itself is rejected. Session is dead.
            is AuthError.InvalidRefreshToken,
            is AuthError.RefreshTokenExpired,
            is AuthError.SessionExpired,
            is AuthError.SessionNotFound,
            is AuthError.AccountLocked,
            is AuthError.AccountDisabled,
            -> {
                Timber.e("Token refresh permanently failed (%s) — logging out", error.code)
                clearSession()
                _authStateFlow.value = AuthState.Unauthenticated(
                    AuthState.Unauthenticated.Reason.SessionExpired,
                )
            }
            // Transient — network issue, server down, etc. Keep the session
            // so the user isn't logged out just because they're offline.
            else -> {
                Timber.w("Token refresh failed transiently (%s) — keeping session", error.code)
                emitAuthenticated()
            }
        }
    }

    // ── Session Persistence ──────────────────────────────────────────

    private fun persistSession(session: SessionInfo, userId: String, email: String) {
        storeTokens(session.accessToken, session.refreshToken)
        diskSource.sessionId = session.sessionId
        diskSource.sessionToken = session.sessionToken
        diskSource.userState = UserState(userId = userId, email = email)
    }

    private fun storeTokens(accessToken: String, refreshToken: String) {
        val expiresAt = (System.currentTimeMillis() / 1_000) + DEFAULT_TOKEN_LIFETIME_SECONDS
        tokenManager.storeTokenData(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = expiresAt,
        )
    }

    private fun clearSession() {
        tokenManager.clearTokenData()
        diskSource.sessionId = null
        diskSource.sessionToken = null
        diskSource.userState = UserState.Companion.Empty
        (userStateFlow as MutableStateFlow).value = UserState.Companion.Empty
    }

    private fun emitAuthenticated() {
        val user = diskSource.userState
        (userStateFlow as MutableStateFlow).value = user
        _authStateFlow.value = AuthState.Authenticated(user)
    }
}
