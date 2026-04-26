package com.application.echo.presentation.otp

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<OtpState, OtpEvent, OtpAction>(
    initialState = savedStateHandle[KEY_STATE] ?: OtpState(
        phoneNumber = savedStateHandle[KEY_PHONE] ?: "",
    ),
) {

    private var resendCooldownJob: Job? = null
    private var expiryJob: Job? = null

    init {
        startResendCooldown()
        startExpiry()
    }

    override fun handleAction(action: OtpAction) {
        when (action) {
            is OtpAction.OnDigitChanged -> onDigitChanged(action.index, action.digit)
            is OtpAction.OnPaste -> onPaste(action.raw)
            is OtpAction.OnBackspace -> onBackspace(action.index)
            is OtpAction.OnFilled -> attemptVerify()
            is OtpAction.OnResendClicked -> attemptResend()
            is OtpAction.OnEditPhoneClicked -> sendEvent(OtpEvent.NavigateBack)
        }
        savedStateHandle[KEY_STATE] = state
    }

    // ─── Digit handling ───────────────────────────────────────────────────────

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
        val filled = List(OTP_LENGTH) { i -> digits[i].toString() }
        setState { state.copy(otpDigits = filled, otpError = null) }
        attemptVerify()
    }

    // ─── Verify ───────────────────────────────────────────────────────────────

    private fun attemptVerify() {
        if (!state.isOtpComplete || state.isLoading) return
        if (state.isExpired) {
            setState { state.copy(otpError = "Code expired. Request a new one.") }
            return
        }

        setState { state.copy(isLoading = true) }
        Timber.d("Verifying OTP: %s for %s", state.otpCode, state.phoneNumber)

        viewModelScope.launch {
            delay(1_500) // 🚧 replace with real API call

            if (state.otpCode == DUMMY_OTP) {
                resendCooldownJob?.cancel()
                expiryJob?.cancel()
                setState { state.copy(isLoading = false) }
                savedStateHandle[KEY_STATE] = state
                sendEvent(OtpEvent.NavigateToHome)
            } else {
                setState {
                    state.copy(
                        isLoading = false,
                        otpDigits = List(OTP_LENGTH) { "" },
                        otpError = "Invalid code. Try again.",
                    )
                }
                savedStateHandle[KEY_STATE] = state
                sendEvent(
                    OtpEvent.ShowSnackbar(
                        message = "Invalid verification code",
                        detail = "Hint: use $DUMMY_OTP (dummy mode)",
                        code = "DUMMY_INVALID_OTP",
                        type = EchoSnackbarType.ERROR,
                    )
                )
            }
        }
    }

    // ─── Resend ───────────────────────────────────────────────────────────────

    private fun attemptResend() {
        if (!state.canResend) return

        setState { state.copy(isLoading = true) }
        Timber.d("Resending OTP to %s", state.phoneNumber)

        viewModelScope.launch {
            delay(1_000) // 🚧 replace with real API call

            setState {
                state.copy(
                    isLoading = false,
                    otpDigits = List(OTP_LENGTH) { "" },
                    otpError = null,
                    resendCooldownSeconds = RESEND_COOLDOWN_SECONDS,
                    expirySeconds = OTP_EXPIRY_SECONDS,
                )
            }
            savedStateHandle[KEY_STATE] = state
            startResendCooldown()
            startExpiry()
        }
    }

    // ─── Timers ───────────────────────────────────────────────────────────────

    private fun startResendCooldown() {
        resendCooldownJob?.cancel()
        resendCooldownJob = viewModelScope.launch {
            repeat(RESEND_COOLDOWN_SECONDS) {
                delay(1_000)
                setState { state.copy(resendCooldownSeconds = state.resendCooldownSeconds - 1) }
                savedStateHandle[KEY_STATE] = state
            }
        }
    }

    private fun startExpiry() {
        expiryJob?.cancel()
        expiryJob = viewModelScope.launch {
            repeat(OTP_EXPIRY_SECONDS) {
                delay(1_000)
                val remaining = state.expirySeconds - 1
                setState { state.copy(expirySeconds = remaining) }
                savedStateHandle[KEY_STATE] = state
                if (remaining == 0) {
                    setState { state.copy(otpError = "Code expired. Tap resend to get a new one.") }
                }
            }
        }
    }

    override fun onCleared() {
        resendCooldownJob?.cancel()
        expiryJob?.cancel()
        super.onCleared()
    }

    companion object {
        private const val KEY_STATE = "otp_state"
        const val KEY_PHONE = "phone_number"
        const val OTP_LENGTH = 6
        const val RESEND_COOLDOWN_SECONDS = 60
        const val OTP_EXPIRY_SECONDS = 300
        const val DUMMY_OTP = "123456" // 🚧 remove when real API is wired
    }
}

// ─── State ────────────────────────────────────────────────────────────────────

@Parcelize
data class OtpState(
    val phoneNumber: String,
    val otpDigits: List<String> = List(6) { "" },
    val otpError: String? = null,
    val isLoading: Boolean = false,
    val resendCooldownSeconds: Int = OtpViewModel.RESEND_COOLDOWN_SECONDS,
    val expirySeconds: Int = OtpViewModel.OTP_EXPIRY_SECONDS,
) : Parcelable {
    val isOtpComplete: Boolean get() = otpDigits.all { it.isNotEmpty() }
    val isExpired: Boolean get() = expirySeconds == 0
    val canResend: Boolean get() = resendCooldownSeconds == 0 && !isLoading
    val otpCode: String get() = otpDigits.joinToString("")

    val expiryFormatted: String get() {
        val m = expirySeconds / 60
        val s = expirySeconds % 60
        return "%d:%02d".format(m, s)
    }
}

// ─── Events ───────────────────────────────────────────────────────────────────

sealed interface OtpEvent {
    data object NavigateToHome : OtpEvent
    data object NavigateBack : OtpEvent
    data class ShowSnackbar(
        val message: String,
        val detail: String,
        val code: String,
        val type: EchoSnackbarType,
    ) : OtpEvent
}

// ─── Actions ──────────────────────────────────────────────────────────────────

sealed interface OtpAction {
    data class OnDigitChanged(val index: Int, val digit: String) : OtpAction
    data class OnPaste(val raw: String) : OtpAction
    data class OnBackspace(val index: Int) : OtpAction
    data object OnFilled : OtpAction
    data object OnResendClicked : OtpAction
    data object OnEditPhoneClicked : OtpAction
}