package com.application.echo.presentation.otp

import android.app.Activity
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.model.PhoneInfo
import com.application.echo.features.auth.model.onError
import com.application.echo.features.otp.model.OtpVerificationState
import com.application.echo.features.otp.repository.OtpRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val otpRepository: OtpRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<OtpScreenState, OtpEvent, OtpAction>(
    initialState = savedStateHandle[KEY_STATE] ?: OtpScreenState(
        phoneInfo = otpRepository.otpStateFlow.value.phoneInfo,
    ),
) {

    private var expiryJob: Job? = null

    init {
        observeOtpState()
        startExpiry()
    }

    // ── Observers ────────────────────────────────────────────────────

    private fun observeOtpState() {
        otpRepository.otpStateFlow
            .onEach { otpState ->
                setState {
                    state.copy(
                        canResend = otpState.canResend,
                        resendCooldownSeconds = otpState.resendCooldownSeconds,
                        resendAttempts = otpState.resendAttempts,
                    )
                }
                when (val s = otpState.state) {
                    is OtpVerificationState.Idle -> Unit

                    is OtpVerificationState.Sending,
                    is OtpVerificationState.Verifying -> {
                        setState { state.copy(isLoading = true, otpError = null) }
                    }

                    is OtpVerificationState.Sent -> {
                        setState {
                            state.copy(
                                isLoading = false,
                                otpError = null,
                            )
                        }
                        startExpiry()
                        savedStateHandle[KEY_STATE] = state
                    }

                    is OtpVerificationState.Success -> {
                        expiryJob?.cancel()
                        setState { state.copy(isLoading = false, otpError = null) }
                        sendEvent(
                            OtpEvent.ShowSnackbar(
                                message = "Verification successful",
                                detail = "Your phone number has been verified.",
                                code = "OTP_VERIFICATION_SUCCESS",
                                type = EchoSnackbarType.SUCCESS,
                            )
                        )
                    }

                    is OtpVerificationState.Failed -> {
                        setState { state.copy(isLoading = false, otpError = s.error) }
                        sendEvent(
                            OtpEvent.ShowSnackbar(
                                message = "Verification failed",
                                detail = s.error,
                                code = "OTP_VERIFICATION_FAILED",
                                type = EchoSnackbarType.ERROR,
                            )
                        )
                    }
                }
                savedStateHandle[KEY_STATE] = state
            }
            .launchIn(viewModelScope)
    }

    // ── Actions ──────────────────────────────────────────────────────

    override fun handleAction(action: OtpAction) {
        when (action) {
            is OtpAction.OnDigitChanged -> onDigitChanged(action.index, action.digit)
            is OtpAction.OnPaste -> onPaste(action.raw)
            is OtpAction.OnBackspace -> onBackspace(action.index)
            is OtpAction.OnFilled -> attemptVerify()
            is OtpAction.OnResendClicked -> sendEvent(OtpEvent.RequestResend)
            is OtpAction.OnResendWithContext -> attemptResend(action.context)
            is OtpAction.OnEditPhoneClicked -> sendEvent(OtpEvent.NavigateBack)
        }
        savedStateHandle[KEY_STATE] = state
    }

    private fun onDigitChanged(index: Int, digit: String) {
        if (index !in 0 until OTP_LENGTH) return
        val updated = state.otpDigits.toMutableList().also { it[index] = digit }
        setState { state.copy(otpDigits = updated, otpError = null) }
    }

    private fun onBackspace(index: Int) {
        if (index !in 0 until OTP_LENGTH) return
        val updated = state.otpDigits.toMutableList().also { it[index] = "" }
        setState { state.copy(otpDigits = updated, otpError = null) }
    }

    private fun onPaste(raw: String) {
        val digits = raw.filter { it.isDigit() }.take(OTP_LENGTH)
        if (digits.length != OTP_LENGTH) return
        setState {
            state.copy(
                otpDigits = List(OTP_LENGTH) { i -> digits[i].toString() },
                otpError = null
            )
        }
        attemptVerify()
    }

    private fun attemptVerify() {
        if (!state.isOtpComplete || state.isLoading) return
        if (state.isExpired) {
            setState { state.copy(otpError = "Code expired — request a new one.") }
            return
        }
        Timber.d("Verifying OTP for %s", state.phoneInfo?.phoneNumber)
        viewModelScope.launch { otpRepository.verifyOtp(state.otpCode) }
    }

    private fun attemptResend(context: Activity) {
        if (!state.canResend || state.isLoading) return
        Timber.d("Resending OTP to %s", state.phoneInfo?.phoneNumber)
        viewModelScope.launch {
            otpRepository.resendOtp(context).also { result ->
                result.onError { error ->
                    sendEvent(
                        OtpEvent.ShowSnackbar(
                            message = "Resend failed",
                            detail = error.message ?: "Could not resend code",
                            code = "OTP_RESEND_FAILED",
                            type = EchoSnackbarType.ERROR,
                        )
                    )
                }
            }
        }
    }

    // ── Expiry Timer ─────────────────────────────────────────────────

    private fun startExpiry() {
        expiryJob?.cancel()
        expiryJob = viewModelScope.launch {
            repeat(OTP_EXPIRY_SECONDS) {
                delay(1_000)
                val remaining = state.expirySeconds - 1
                setState { state.copy(expirySeconds = remaining) }
                savedStateHandle[KEY_STATE] = state
                if (remaining == 0) {
                    setState { state.copy(otpError = "Code expired — tap resend to get a new one.") }
                }
            }
        }
    }

    override fun onCleared() {
        expiryJob?.cancel()
        super.onCleared()
    }

    companion object {
        private const val KEY_STATE = "otp_state"
        const val OTP_LENGTH = 6
        const val OTP_EXPIRY_SECONDS = 300
    }
}

// ── State ────────────────────────────────────────────────────────────

@Parcelize
data class OtpScreenState(
    val phoneInfo: PhoneInfo?,
    val otpDigits: List<String> = List(6) { "" },
    val otpError: String? = null,
    val isLoading: Boolean = false,
    val expirySeconds: Int = OTP_EXPIRY_SECONDS,
    val canResend: Boolean = false,
    val resendCooldownSeconds: Int = 0,
    val resendAttempts: Int = 0,
) : Parcelable {
    val isOtpComplete: Boolean get() = otpDigits.all { it.isNotEmpty() }
    val isExpired: Boolean get() = expirySeconds == 0
    val otpCode: String get() = otpDigits.joinToString("")
    val expiryFormatted: String
        get() {
            val m = expirySeconds / 60
            val s = expirySeconds % 60
            return "%d:%02d".format(m, s)
        }

    companion object {
        const val OTP_EXPIRY_SECONDS = 300
    }
}

// ── Events ───────────────────────────────────────────────────────────

sealed interface OtpEvent {
    data object NavigateBack : OtpEvent
    data object RequestResend : OtpEvent
    data class ShowSnackbar(
        val message: String,
        val detail: String,
        val code: String,
        val type: EchoSnackbarType,
    ) : OtpEvent
}

// ── Actions ──────────────────────────────────────────────────────────

sealed interface OtpAction {
    data class OnDigitChanged(val index: Int, val digit: String) : OtpAction
    data class OnPaste(val raw: String) : OtpAction
    data class OnBackspace(val index: Int) : OtpAction
    data object OnFilled : OtpAction
    data object OnResendClicked : OtpAction
    data class OnResendWithContext(val context: Activity) : OtpAction
    data object OnEditPhoneClicked : OtpAction
}