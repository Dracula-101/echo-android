package com.application.echo.features.auth.repository

import android.app.Activity
import com.application.echo.api.auth.AuthError
import com.application.echo.features.auth.model.AuthResult
import com.application.echo.api.auth.LoginResponse
import com.application.echo.api.auth.RegisterResponse
import com.application.echo.api.auth.SessionInfo
import com.application.echo.features.auth.model.fold
import com.application.echo.features.auth.model.onSuccess
import com.application.echo.api.manager.AuthTokenManager
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.core.network.util.toNetworkException
import com.application.echo.features.auth.datasource.disk.AuthDiskSource
import com.application.echo.features.auth.datasource.network.AuthNetworkSource
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.model.OtpState
import com.application.echo.features.auth.model.OtpVerificationState
import com.application.echo.features.auth.model.PhoneInfo
import com.application.echo.features.auth.model.UserState
import com.application.echo.features.profile.datasource.disk.ProfileDiskSource
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val OTP_TIMEOUT_SECONDS = 120L

class AuthRepositoryImpl @Inject constructor(
    private val networkSource: AuthNetworkSource,
    private val authDiskSource: AuthDiskSource,
    private val profileDiskSource: ProfileDiskSource,
    private val tokenManager: AuthTokenManager,
    private val firebaseAuth: FirebaseAuth,
    @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val sessionMutex = Mutex()

    private val _authStateFlow = MutableStateFlow<AuthState>(AuthState.Initializing)
    override val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    override val userStateFlow: MutableStateFlow<UserState> =
        MutableStateFlow(authDiskSource.userState)

    private val _otpState = MutableStateFlow(
        OtpState(phoneInfo = authDiskSource.cachedPhoneInfo, state = OtpVerificationState.Idle)
    )
    override val otpStateFlow: StateFlow<OtpState> = _otpState.asStateFlow()

    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    init {
        scope.launch { restoreSession() }
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
        acceptTerms: Boolean,
    ): AuthResult<RegisterResponse> = sessionMutex.withLock {
        authDiskSource.registerEmail = email
        authDiskSource.registerPassword = password
        networkSource.register(email, password, acceptTerms).fold(
            onSuccess = { response ->
                storeTokens(response.accessToken, response.refreshToken, response.expiresAt)
                if (!response.requiresEmailVerification) {
                    _authStateFlow.value = AuthState.CreateProfile(response.userId)
                }
                AuthResult.Success(response)
            },
            onError = { error ->
                Timber.e("Registration failed: %s", error.code)
                AuthResult.Error(error)
            },
        )
    }

    override fun logout() {
        clearSession()
        _authStateFlow.value = AuthState.Unauthenticated(AuthState.Unauthenticated.Reason.LoggedOut)
    }

    // ── Session Restoration ──────────────────────────────────────────

    /**
     * Resolution order:
     *
     *  1. Profile creation in progress (tokens exist, userState not yet written)
     *  2. Stored user + valid token          → Authenticated  (clears any stale OTP state)
     *  3. Stored user + expired token        → attempt refresh
     *  4. Stored user + no token data        → stale session, clear + Unauthenticated
     *  5. OTP was in progress, no session    → OtpVerification
     *  6. Nothing                            → Unauthenticated
     *
     * Token validity is checked BEFORE OTP state so a crash between persistSession
     * and clearPhoneRegisterSession never strands the user on the OTP screen.
     */
    private suspend fun restoreSession() = sessionMutex.withLock {
        if (profileDiskSource.creatingProfileState) {
            val userId = profileDiskSource.creatingProfileUserId
            if (userId != null) {
                Timber.d("Resuming profile creation for user %s", userId)
                _authStateFlow.value = AuthState.CreateProfile(userId)
                return
            }
        }

        if (authDiskSource.userState != UserState.Empty) {
            if (tokenManager.isTokenValid) {
                Timber.d("Valid token found — authenticated")
                clearPhoneRegisterSession()
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

            Timber.d("Token expired — attempting refresh")
            networkSource.refreshToken(tokenData.refreshToken).fold(
                onSuccess = { response ->
                    storeTokens(response.accessToken, response.refreshToken, response.expiresAt)
                    clearPhoneRegisterSession()
                    emitAuthenticated()
                },
                onError = { error -> handleRefreshError(error) },
            )
            return
        }

        if (authDiskSource.cachedPhoneInfo != null) {
            Timber.d("OTP flow was in progress — resuming")
            _authStateFlow.value = AuthState.OtpVerification(authDiskSource.cachedPhoneInfo!!)
            return
        }

        Timber.d("No session found — unauthenticated")
        _authStateFlow.value = AuthState.Unauthenticated()
    }

    // ── Refresh Error Handling ────────────────────────────────────────

    private fun handleRefreshError(error: AuthError) {
        val isTerminal = error is AuthError.InvalidRefreshToken
                || error is AuthError.RefreshTokenExpired
                || error is AuthError.SessionExpired
                || error is AuthError.SessionNotFound
                || error is AuthError.AccountLocked
                || error is AuthError.AccountDisabled

        if (isTerminal) {
            Timber.e("Token refresh permanently failed (%s) — logging out", error.code)
            clearSession()
            _authStateFlow.value = AuthState.Unauthenticated(AuthState.Unauthenticated.Reason.SessionExpired)
        } else {
            Timber.w("Token refresh failed transiently (%s) — keeping session", error.code)
            emitAuthenticated()
        }
    }

    // ── OTP ──────────────────────────────────────────────────────────

    override suspend fun sendOtp(phoneInfo: PhoneInfo, context: Activity): AuthResult<Unit> {
        authDiskSource.cachedPhoneInfo = phoneInfo
        return startPhoneVerification(phoneInfo, context, resendToken = null)
    }

    override suspend fun resendOtp(context: Activity): AuthResult<Unit> {
        val token = resendToken
            ?: return AuthResult.Error(
                AuthError.Unknown("NO_RESEND_TOKEN", "No active OTP session to resend")
            )
        val phoneInfo = authDiskSource.cachedPhoneInfo
            ?: return AuthResult.Error(
                AuthError.Unknown("NO_PHONE_INFO", "No cached phone info for OTP resend")
            )
        return startPhoneVerification(phoneInfo, context, resendToken = token)
    }

    private fun startPhoneVerification(
        phoneInfo: PhoneInfo,
        context: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken?,
    ): AuthResult<Unit> {
        _otpState.value = otpState(phoneInfo, OtpVerificationState.Sending)
        val builder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneInfo.phoneNumber)
            .setActivity(context)
            .setTimeout(OTP_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .setCallbacks(buildVerificationCallbacks(phoneInfo))
        resendToken?.let { builder.setForceResendingToken(it) }
        return try {
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
            AuthResult.Success(Unit)
        } catch (e: FirebaseAuthMissingActivityForRecaptchaException) {
            Timber.e(e, "reCAPTCHA activity missing")
            AuthResult.Error(AuthError.Unknown("RECAPTCHA_ERROR", "Verification requires a valid Activity"))
        } catch (e: FirebaseException) {
            val error = mapFirebaseException(e)
            Timber.e(e, "Phone verification failed: %s", error.code)
            AuthResult.Error(error)
        }
    }

    private fun buildVerificationCallbacks(
        phoneInfo: PhoneInfo,
    ) = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Timber.d("Auto-verification triggered for %s", phoneInfo.phoneNumber)
            signInWithCredential(credential)
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            val error = mapFirebaseException(exception)
            Timber.e(exception, "OTP send failed: %s", error.code)
            _otpState.value = otpState(phoneInfo, OtpVerificationState.Failed(error.message))
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Timber.d("OTP code sent to %s", phoneInfo.phoneNumber)
            resendToken = token
            authDiskSource.cachedVerificationId = verificationId
            _otpState.value = otpState(phoneInfo, OtpVerificationState.Sent)
            _authStateFlow.value = AuthState.OtpVerification(phoneInfo)
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        val phoneInfo = authDiskSource.cachedPhoneInfo ?: run {
            Timber.w("signInWithCredential called with no cached phone info")
            return
        }
        _otpState.value = otpState(phoneInfo, OtpVerificationState.Verifying)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user ?: run {
                    Timber.e("Firebase sign-in succeeded but user is null")
                    _otpState.value = otpState(phoneInfo, OtpVerificationState.Failed("Sign-in returned no user"))
                    return@addOnSuccessListener
                }
                user.getIdToken(true)
                    .addOnSuccessListener { idTokenResult ->
                        val token = idTokenResult.token ?: run {
                            Timber.e("ID token result returned null token")
                            _otpState.value = otpState(phoneInfo, OtpVerificationState.Failed("Failed to retrieve ID token"))
                            firebaseAuth.signOut()
                            return@addOnSuccessListener
                        }
                        scope.launch {
                            networkSource.loginWithToken(token).fold(
                                onSuccess = { response ->
                                    persistSession(response.session, response.user.id, response.user.email)
                                    clearPhoneRegisterSession()
                                    _otpState.value = otpState(null, OtpVerificationState.Success)
                                    emitAuthenticated()
                                },
                                onError = { error ->
                                    Timber.e("Backend token exchange failed: %s", error.code)
                                    _otpState.value = otpState(phoneInfo, OtpVerificationState.Failed(mapBackendOtpError(error)))
                                    firebaseAuth.signOut()
                                },
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        Timber.e(e, "getIdToken failed")
                        _otpState.value = otpState(phoneInfo, OtpVerificationState.Failed("Could not retrieve verification token"))
                        firebaseAuth.signOut()
                    }
            }
            .addOnFailureListener { e ->
                val state = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> OtpVerificationState.Failed("Incorrect verification code")
                    is FirebaseAuthInvalidUserException -> OtpVerificationState.Failed("This account has been disabled")
                    is FirebaseTooManyRequestsException -> OtpVerificationState.Failed("Too many attempts — please wait and try again")
                    is FirebaseNetworkException -> OtpVerificationState.Failed("No internet connection")
                    else -> OtpVerificationState.Failed(e.message ?: "Verification failed")
                }
                Timber.e(e, "signInWithCredential failed")
                _otpState.value = otpState(phoneInfo, state)
            }
    }

    override suspend fun verifyOtp(otp: String): AuthResult<Unit> {
        val verificationId = authDiskSource.cachedVerificationId
            ?: return AuthResult.Error(AuthError.InvalidPhoneNumber("No OTP session in progress"))
        if (otp.isBlank() || otp.length != 6 || !otp.all { it.isDigit() }) {
            return AuthResult.Error(AuthError.InvalidOTP("OTP must be a 6-digit number"))
        }
        return try {
            signInWithCredential(PhoneAuthProvider.getCredential(verificationId, otp))
            AuthResult.Success(Unit)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e(e, "verifyOtp: invalid credential")
            AuthResult.Error(AuthError.InvalidOTP("Incorrect verification code"))
        } catch (e: FirebaseNetworkException) {
            Timber.e(e, "verifyOtp: network error")
            AuthResult.Error(AuthError.NetworkError("No internet connection", cause = e.toNetworkException()))
        } catch (e: FirebaseException) {
            val error = mapFirebaseException(e)
            Timber.e(e, "verifyOtp failed: %s", error.code)
            AuthResult.Error(error)
        }
    }

    // ── Firebase Error Mapping ────────────────────────────────────────

    private fun mapFirebaseException(e: FirebaseException): AuthError = when (e) {
        is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidOTP("Incorrect verification code")
        is FirebaseAuthInvalidUserException -> AuthError.AccountDisabled("This account has been disabled")
        is FirebaseTooManyRequestsException -> AuthError.Unknown("QUOTA_EXCEEDED", "SMS quota exceeded — try again later")
        is FirebaseNetworkException -> AuthError.NetworkError("No internet connection", cause = e.toNetworkException())
        else -> AuthError.Unknown("FIREBASE_ERROR", e.message ?: "An unexpected error occurred")
    }

    private fun mapBackendOtpError(error: AuthError): String = when (error) {
        is AuthError.AccountLocked -> "Your account has been locked"
        is AuthError.AccountDisabled -> "Your account has been disabled"
        is AuthError.NetworkError -> "No internet connection"
        is AuthError.SessionExpired -> "Session expired — please try again"
        else -> "Verification failed — please try again"
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

    private fun clearPhoneRegisterSession() {
        authDiskSource.cachedPhoneInfo = null
        authDiskSource.cachedVerificationId = null
        resendToken = null
    }

    private fun clearSession() {
        tokenManager.clearTokenData()
        authDiskSource.sessionId = null
        authDiskSource.sessionToken = null
        authDiskSource.userState = UserState.Empty
        clearPhoneRegisterSession()
        userStateFlow.value = UserState.Empty
    }

    private fun emitAuthenticated() {
        val user = authDiskSource.userState
        userStateFlow.value = user
        _authStateFlow.value = AuthState.Authenticated(user)
    }

    private fun otpState(phoneInfo: PhoneInfo?, state: OtpVerificationState) =
        OtpState(phoneInfo = phoneInfo, state = state)
}