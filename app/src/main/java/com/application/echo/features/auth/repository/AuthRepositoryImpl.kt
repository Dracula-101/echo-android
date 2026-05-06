package com.application.echo.features.auth.repository

import com.application.echo.api.auth.AuthError
import com.application.echo.api.auth.LoginResponse
import com.application.echo.api.auth.RegisterResponse
import com.application.echo.api.auth.SessionInfo
import com.application.echo.api.manager.AuthTokenManager
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.features.auth.datasource.disk.AuthDiskSource
import com.application.echo.features.auth.datasource.network.AuthNetworkSource
import com.application.echo.features.auth.model.AuthResult
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.model.UserState
import com.application.echo.features.auth.model.fold
import com.application.echo.features.auth.model.onSuccess
import com.application.echo.features.otp.datasource.disk.OtpDiskSource
import com.application.echo.features.otp.model.OtpAuthEvent
import com.application.echo.features.otp.model.OtpVerificationState
import com.application.echo.features.otp.repository.OtpRepository
import com.application.echo.features.profile.datasource.disk.ProfileDiskSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val networkSource: AuthNetworkSource,
    private val authDiskSource: AuthDiskSource,
    private val otpDiskSource: OtpDiskSource,
    private val profileDiskSource: ProfileDiskSource,
    private val tokenManager: AuthTokenManager,
    private val otpRepository: OtpRepository,
    @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val sessionMutex = Mutex()

    private val _authStateFlow = MutableStateFlow<AuthState>(AuthState.Initializing)
    override val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    override val userStateFlow: MutableStateFlow<UserState> =
        MutableStateFlow(authDiskSource.userState)

    init {
        scope.launch { restoreSession() }
        scope.launch { observeOtpAuthEvents() }
        scope.launch { observeOtpStateChanges() }
    }

    // ── OTP Event Bridge ─────────────────────────────────────────────
    private suspend fun observeOtpAuthEvents() {
        otpRepository.authEventFlow.collect { event ->
            when (event) {
                is OtpAuthEvent.Authenticated -> sessionMutex.withLock {
                    persistSession(event.response.session, event.response.user.id, event.response.user.email)
                    otpRepository.clearOtpSession()
                    emitAuthenticated()
                }
                is OtpAuthEvent.CreateAccount -> {
                    // This case should be hit when the user has successfully verified their phone number
                    // but does not have an account yet. We transition them to the CreateProfile state,
                    // which will prompt them to create a profile and complete registration.
                    _authStateFlow.value = AuthState.CreateProfile(event.phoneInfo)
                }
            }
        }
    }

    private suspend fun observeOtpStateChanges() {
        otpRepository.otpStateFlow
            .distinctUntilChanged { old, new -> old.state::class == new.state::class }
            .collect { otpState ->
                if (otpState.state is OtpVerificationState.Sent && otpState.phoneInfo != null) {
                    _authStateFlow.value = AuthState.OtpVerification(otpState.phoneInfo)
                }
            }
    }

    // ── Auth ─────────────────────────────────────────────────────────

    override suspend fun login(
        email: String,
        password: String,
    ): AuthResult<LoginResponse> = sessionMutex.withLock {
        networkSource.login(email, password).also { result ->
            result.onSuccess { response ->
                persistSession(response.session, response.user.id, response.user.email)
                emitAuthenticated()
            }
        }
    }

    override suspend fun loginWithToken(token: String): AuthResult<LoginResponse> = sessionMutex.withLock {
        networkSource.loginWithToken(token).also { result ->
            result.onSuccess { response ->
                persistSession(response.session, response.user.id, response.user.email)
                emitAuthenticated()
            }
        }
    }

    override suspend fun autoLogin() {
        val email = authDiskSource.registerEmail
        val password = authDiskSource.registerPassword
        if (email != null && password != null) {
            login(email, password).onSuccess {
                authDiskSource.registerEmail = null
                authDiskSource.registerPassword = null
            }
        } else {
            Timber.w("Auto-login skipped: no stored credentials")
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        phoneNumber: String,
        phoneCountryCode: String,
        acceptTerms: Boolean,
    ): AuthResult<RegisterResponse> = sessionMutex.withLock {
        networkSource.register(email, password, phoneNumber, phoneCountryCode, acceptTerms).fold(
            onSuccess = { response ->
                // TODO: handle registration response properly (e.g. if next step is to verify OTP, transition to OTP screen instead of treating as success)
                AuthResult.Success(response)
            },
            onError = { error ->
                Timber.e("Registration failed: %s", error.code)
                AuthResult.Error(error)
            },
        )
    }

    override fun setPreRegistrationInfo(
        email: String?,
        password: String?,
        phoneNumber: String?
    ): AuthResult<Unit> {
        email?.let { authDiskSource.registerEmail = it }
        password?.let { authDiskSource.registerPassword = it }
        phoneNumber?.let { authDiskSource.registerPhoneNumber = it }
        return AuthResult.Success(Unit)
    }

    override fun logout() {
        clearSession()
        _authStateFlow.value = AuthState.Unauthenticated(AuthState.Unauthenticated.Reason.LoggedOut)
    }

    // ── Session Restoration ──────────────────────────────────────────

    private suspend fun restoreSession() = sessionMutex.withLock {
        if (profileDiskSource.creatingProfileState) {
            val phoneInfo = otpRepository.cachedPhoneInfo
            if (phoneInfo != null) {
                Timber.d("Resuming profile creation for user %s", phoneInfo)
                _authStateFlow.value = AuthState.CreateProfile(phoneInfo)
                return
            }
        }

        if (authDiskSource.userState != UserState.Empty) {
            if (tokenManager.isTokenValid) {
                Timber.d("Valid token found — authenticated")
                otpRepository.clearOtpSession()
                emitAuthenticated()
                return
            }

            val tokenData = tokenManager.getLatestAuthTokenData()
            if (tokenData == null) {
                Timber.w("Stored user but no token data — clearing stale session")
                clearSession()
                _authStateFlow.value = AuthState.Unauthenticated(AuthState.Unauthenticated.Reason.SessionExpired)
                return
            }

            Timber.d("Token expired — resolving from disk, refreshing in background")
            otpRepository.clearOtpSession()
            emitAuthenticated()
            refreshTokenInBackground(tokenData.refreshToken)
            return
        }

        if (otpDiskSource.cachedPhoneInfo != null) {
            Timber.d("OTP flow in progress — resuming")
            _authStateFlow.value = AuthState.OtpVerification(otpDiskSource.cachedPhoneInfo!!)
            return
        }

        Timber.d("No session found — unauthenticated")
        _authStateFlow.value = AuthState.Unauthenticated()
    }

    // ── Token Refresh ────────────────────────────────────────────────

    private fun refreshTokenInBackground(refreshToken: String) {
        scope.launch {
            networkSource.refreshToken(refreshToken).fold(
                onSuccess = { response ->
                    storeTokens(response.accessToken, response.refreshToken, response.expiresAt)
                    Timber.d("Background token refresh succeeded")
                },
                onError = { error ->
                    val isTerminal = error is AuthError.InvalidRefreshToken
                            || error is AuthError.RefreshTokenExpired
                            || error is AuthError.SessionExpired
                            || error is AuthError.SessionNotFound
                            || error is AuthError.AccountLocked
                            || error is AuthError.AccountDisabled
                    if (isTerminal) {
                        Timber.e("Background token refresh failed terminally (%s) — logging out", error.code)
                        sessionMutex.withLock {
                            clearSession()
                            _authStateFlow.value = AuthState.Unauthenticated(AuthState.Unauthenticated.Reason.SessionExpired)
                        }
                    } else {
                        Timber.w("Background token refresh failed transiently (%s) — keeping session", error.code)
                    }
                },
            )
        }
    }

    // ── Session Persistence ──────────────────────────────────────────

    private fun persistSession(session: SessionInfo, userId: String, email: String) {
        storeTokens(session.accessToken, session.refreshToken, session.expiresAt)
        authDiskSource.sessionId = session.sessionId
        authDiskSource.sessionToken = session.sessionToken
        authDiskSource.userState = UserState(userId = userId, email = email)
    }

    private fun storeTokens(accessToken: String, refreshToken: String, expiresAt: String) {
        tokenManager.storeTokenData(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresAt = expiresAt,
        )
    }

    private fun clearSession() {
        tokenManager.clearTokenData()
        authDiskSource.sessionId = null
        authDiskSource.sessionToken = null
        authDiskSource.userState = UserState.Empty
        otpRepository.clearOtpSession()
        userStateFlow.value = UserState.Empty
    }

    private fun emitAuthenticated() {
        val user = authDiskSource.userState
        userStateFlow.value = user
        _authStateFlow.value = AuthState.Authenticated(user)
    }
}