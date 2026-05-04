package com.application.echo.features.otp.repository

import android.app.Activity
import com.application.echo.api.auth.AuthError
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.core.network.util.toNetworkException
import com.application.echo.features.auth.datasource.network.AuthNetworkSource
import com.application.echo.features.auth.model.AuthResult
import com.application.echo.features.auth.model.PhoneInfo
import com.application.echo.features.auth.model.fold
import com.application.echo.features.otp.datasource.disk.OtpDiskSource
import com.application.echo.features.otp.model.OtpAuthEvent
import com.application.echo.features.otp.model.OtpState
import com.application.echo.features.otp.model.OtpVerificationState
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val OTP_TIMEOUT_SECONDS = 120L
private const val RESEND_COOLDOWN_SECONDS = 60
private const val MAX_RESEND_ATTEMPTS = 3

class OtpRepositoryImpl @Inject constructor(
    private val networkSource: AuthNetworkSource,
    private val otpDiskSource: OtpDiskSource,
    private val firebaseAuth: FirebaseAuth,
    @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
) : OtpRepository {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private val _otpState = MutableStateFlow(
        OtpState(
            phoneInfo = otpDiskSource.cachedPhoneInfo,
            state = OtpVerificationState.Idle,
        )
    )
    override val otpStateFlow: StateFlow<OtpState> = _otpState.asStateFlow()

    private val _authEventFlow = MutableSharedFlow<OtpAuthEvent>(extraBufferCapacity = 1)
    override val authEventFlow: SharedFlow<OtpAuthEvent> = _authEventFlow.asSharedFlow()

    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var cooldownJob: Job? = null

    // ── Public API ───────────────────────────────────────────────────

    override suspend fun sendOtp(phoneInfo: PhoneInfo, context: Activity): AuthResult<Unit> {
        otpDiskSource.cachedPhoneInfo = phoneInfo
        _otpState.value = OtpState(
            phoneInfo = phoneInfo,
            state = OtpVerificationState.Idle,
            canResend = false,
            resendCooldownSeconds = 0,
            resendAttempts = 0,
        )
        return startPhoneVerification(phoneInfo, context, resendToken = null)
    }

    override suspend fun resendOtp(context: Activity): AuthResult<Unit> {
        val phoneInfo = otpDiskSource.cachedPhoneInfo
            ?: return AuthResult.Error(
                AuthError.Unknown("NO_PHONE_INFO", "No active OTP session")
            )
        val token = resendToken
            ?: return AuthResult.Error(
                AuthError.Unknown("NO_RESEND_TOKEN", "No resend token available — send OTP first")
            )
        if (!_otpState.value.canResend) {
            return AuthResult.Error(
                AuthError.Unknown("RESEND_COOLDOWN", "Please wait ${_otpState.value.resendCooldownSeconds}s before resending")
            )
        }
        if (_otpState.value.resendAttempts >= MAX_RESEND_ATTEMPTS) {
            return AuthResult.Error(
                AuthError.Unknown("MAX_RESEND_EXCEEDED", "Maximum resend attempts reached — please restart verification")
            )
        }
        return startPhoneVerification(phoneInfo, context, resendToken = token)
    }

    override suspend fun verifyOtp(otp: String): AuthResult<Unit> {
        val verificationId = otpDiskSource.cachedVerificationId
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

    override fun clearOtpSession() {
        cooldownJob?.cancel()
        otpDiskSource.cachedPhoneInfo = null
        otpDiskSource.cachedVerificationId = null
        resendToken = null
        _otpState.value = OtpState(phoneInfo = null, state = OtpVerificationState.Idle)
    }

    // ── Phone Verification ───────────────────────────────────────────

    private fun startPhoneVerification(
        phoneInfo: PhoneInfo,
        context: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken?,
    ): AuthResult<Unit> {
        _otpState.update { it.copy(state = OtpVerificationState.Sending, canResend = false) }
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
            _otpState.update { it.copy(state = OtpVerificationState.Failed("Verification requires a valid Activity")) }
            AuthResult.Error(AuthError.Unknown("RECAPTCHA_ERROR", "Verification requires a valid Activity"))
        } catch (e: FirebaseException) {
            val error = mapFirebaseException(e)
            Timber.e(e, "startPhoneVerification failed: %s", error.code)
            _otpState.update { it.copy(state = OtpVerificationState.Failed(error.message)) }
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
            _otpState.update { it.copy(state = OtpVerificationState.Failed(error.message)) }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Timber.d("OTP code sent to %s", phoneInfo.phoneNumber)
            resendToken = token
            otpDiskSource.cachedVerificationId = verificationId
            val attempts = _otpState.value.resendAttempts + 1
            _otpState.value = OtpState(
                phoneInfo = phoneInfo,
                state = OtpVerificationState.Sent,
                canResend = false,
                resendCooldownSeconds = RESEND_COOLDOWN_SECONDS,
                resendAttempts = attempts,
            )
            startResendCooldown()
        }
    }

    private fun startResendCooldown() {
        cooldownJob?.cancel()
        cooldownJob = scope.launch {
            for (remaining in RESEND_COOLDOWN_SECONDS downTo 1) {
                _otpState.update { it.copy(resendCooldownSeconds = remaining, canResend = false) }
                delay(1_000)
            }
            _otpState.update { it.copy(resendCooldownSeconds = 0, canResend = true) }
            Timber.d("Resend cooldown expired")
        }
    }

    // ── Credential Sign-In ───────────────────────────────────────────

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        val phoneInfo = otpDiskSource.cachedPhoneInfo ?: run {
            Timber.w("signInWithCredential called with no cached phone info")
            return
        }
        _otpState.update { it.copy(state = OtpVerificationState.Verifying) }
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user ?: run {
                    Timber.e("Firebase sign-in succeeded but user is null")
                    _otpState.update { it.copy(state = OtpVerificationState.Failed("Sign-in returned no user")) }
                    return@addOnSuccessListener
                }
                user.getIdToken(true)
                    .addOnSuccessListener { idTokenResult ->
                        val token = idTokenResult.token ?: run {
                            Timber.e("ID token result returned null token")
                            _otpState.update { it.copy(state = OtpVerificationState.Failed("Failed to retrieve ID token")) }
                            firebaseAuth.signOut()
                            return@addOnSuccessListener
                        }
                        scope.launch { exchangeTokenWithBackend(token, phoneInfo) }
                    }
                    .addOnFailureListener { e ->
                        Timber.e(e, "getIdToken failed")
                        _otpState.update { it.copy(state = OtpVerificationState.Failed("Could not retrieve verification token")) }
                        firebaseAuth.signOut()
                    }
            }
            .addOnFailureListener { e ->
                val message = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Incorrect verification code"
                    is FirebaseAuthInvalidUserException -> "This account has been disabled"
                    is FirebaseTooManyRequestsException -> "Too many attempts — please wait and try again"
                    is FirebaseNetworkException -> "No internet connection"
                    else -> e.message ?: "Verification failed"
                }
                Timber.e(e, "signInWithCredential failed")
                _otpState.update { it.copy(state = OtpVerificationState.Failed(message)) }
            }
    }

    private suspend fun exchangeTokenWithBackend(token: String, phoneInfo: PhoneInfo) {
        networkSource.loginWithToken(token).fold(
            onSuccess = { response ->
                _otpState.update { it.copy(state = OtpVerificationState.Success) }
                _authEventFlow.emit(OtpAuthEvent.Authenticated(response))
            },
            onError = { error ->
                when (error) {
                    is AuthError.UserNotFound -> {
                        Timber.i("User not found — routing to account creation for %s", phoneInfo.phoneNumber)
                        _authEventFlow.emit(OtpAuthEvent.CreateAccount(phoneInfo))
                    }
                    else -> {
                        Timber.e("Backend token exchange failed: %s", error.code)
                        _otpState.update { it.copy(state = OtpVerificationState.Failed(mapBackendOtpError(error))) }
                        firebaseAuth.signOut()
                    }
                }
            },
        )
    }

    // ── Error Mapping ────────────────────────────────────────────────

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
}