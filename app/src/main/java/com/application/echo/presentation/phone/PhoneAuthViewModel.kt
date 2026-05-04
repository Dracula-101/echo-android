package com.application.echo.presentation.phone

import android.app.Activity
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.model.PhoneInfo
import com.application.echo.features.country.model.Country
import com.application.echo.features.country.repository.CountryRepository
import com.application.echo.features.otp.model.OtpVerificationState
import com.application.echo.features.otp.repository.OtpRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val countryRepository: CountryRepository,
    private val otpRepository: OtpRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<PhoneAuthState, PhoneAuthEvent, PhoneAuthAction>(
    initialState = savedStateHandle[KEY_STATE] ?: PhoneAuthState(
        selectedCountry = countryRepository.detectCurrentCountry().toUiCountry(),
    ),
) {

    val countries: List<UiCountry> by lazy {
        countryRepository.all.map { it.toUiCountry() }
    }

    init {
        observeOtpState()
    }

    override fun handleAction(action: PhoneAuthAction) {
        when (action) {
            is PhoneAuthAction.OnCountryChanged -> onCountryChanged(action.country)
            is PhoneAuthAction.OnPhoneNumberChanged -> onPhoneNumberChanged(action.phoneNumber)
            is PhoneAuthAction.OnSendOtpClicked -> attemptSendOtp(action.activity)
        }
        savedStateHandle[KEY_STATE] = state
    }

    private fun onCountryChanged(country: UiCountry) {
        setState { state.copy(selectedCountry = country, phoneNumberError = null) }
    }

    private fun onPhoneNumberChanged(phoneNumber: String) {
        val sanitized = phoneNumber
            .filter { it.isDigit() }
            .trimStart('0')
            .take(MAX_LOCAL_PHONE_DIGITS)
        setState { state.copy(phoneNumber = sanitized, phoneNumberError = null) }
    }

    private fun attemptSendOtp(activity: Activity) {
        val validated = validatePhoneNumber()
        if (validated.hasPhoneError) {
            setState { validated }
            savedStateHandle[KEY_STATE] = state
            return
        }
        setState { state.copy(isLoading = true) }
        Timber.d("Sending OTP to %s", state.e164PhoneNumber)
        viewModelScope.launch {
            otpRepository.sendOtp(
                phoneInfo = PhoneInfo(
                    country = Country(
                        isoCode = state.selectedCountry.isoCode,
                        name = state.selectedCountry.name,
                        dialCode = state.selectedCountry.dialCode,
                        minDigits = state.selectedCountry.minDigits,
                        maxDigits = state.selectedCountry.maxDigits,
                    ),
                    phoneNumber = state.e164PhoneNumber,
                ),
                context = activity,
            )
        }
    }

    private fun observeOtpState() {
        otpRepository.otpStateFlow
            .onEach { otpState ->
                when (val s = otpState.state) {
                    is OtpVerificationState.Sending -> {
                        setState { state.copy(isLoading = true) }
                    }

                    is OtpVerificationState.Sent -> {
                        setState { state.copy(isLoading = false) }
                    }

                    is OtpVerificationState.Failed -> {
                        setState { state.copy(isLoading = false) }
                        sendEvent(
                            PhoneAuthEvent.ShowSnackbar(
                                message = "Failed to send OTP",
                                detail = s.error,
                                code = "OTP_SEND_ERROR",
                                type = EchoSnackbarType.ERROR,
                            )
                        )
                    }

                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    private fun validatePhoneNumber(): PhoneAuthState {
        val error = when {
            state.phoneNumber.isBlank() -> "Phone number is required"
            state.phoneNumber.length < state.selectedCountry.minDigits -> "Enter at least ${state.selectedCountry.minDigits} digits"
            state.phoneNumber.length > state.selectedCountry.maxDigits -> "Enter no more than ${state.selectedCountry.maxDigits} digits"
            else -> null
        }
        return state.copy(phoneNumberError = error)
    }

    companion object {
        private const val KEY_STATE = "phone_auth_state"
        private const val MAX_LOCAL_PHONE_DIGITS = 15
    }
}

// ── UiCountry ────────────────────────────────────────────────────────

@Parcelize
data class UiCountry(
    val isoCode: String,
    val name: String,
    val dialCode: String,
    val minDigits: Int,
    val maxDigits: Int,
) : Parcelable

fun Country.toUiCountry() = UiCountry(
    isoCode = isoCode,
    name = name,
    dialCode = dialCode,
    minDigits = minDigits,
    maxDigits = maxDigits,
)

// ── State ────────────────────────────────────────────────────────────

@Parcelize
data class PhoneAuthState(
    val selectedCountry: UiCountry,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val isLoading: Boolean = false,
) : Parcelable {
    val hasPhoneError: Boolean get() = phoneNumberError != null
    val e164PhoneNumber: String get() = "+${selectedCountry.dialCode}${phoneNumber.trimStart('0')}"
    val formattedPhoneNumber: String get() = "+${selectedCountry.dialCode} $phoneNumber"
}

// ── Events ───────────────────────────────────────────────────────────

sealed interface PhoneAuthEvent {
    data class ShowSnackbar(
        val message: String,
        val detail: String,
        val code: String,
        val type: EchoSnackbarType,
    ) : PhoneAuthEvent
}

// ── Actions ──────────────────────────────────────────────────────────

sealed interface PhoneAuthAction {
    data class OnCountryChanged(val country: UiCountry) : PhoneAuthAction
    data class OnPhoneNumberChanged(val phoneNumber: String) : PhoneAuthAction
    data class OnSendOtpClicked(val activity: Activity) : PhoneAuthAction
}