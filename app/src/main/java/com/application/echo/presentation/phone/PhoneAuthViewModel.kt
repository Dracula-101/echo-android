package com.application.echo.presentation.phone

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.country.model.Country
import com.application.echo.features.country.repository.CountryRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val countryRepository: CountryRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<PhoneAuthState, PhoneAuthEvent, PhoneAuthAction>(
    initialState = savedStateHandle[KEY_STATE] ?: PhoneAuthState(
        selectedCountry = countryRepository.detectCurrentCountry().toUiCountry(),
    ),
) {

    val countries: List<UiCountry> by lazy {
        countryRepository.all.map { it.toUiCountry() }
    }

    override fun handleAction(action: PhoneAuthAction) {
        when (action) {
            is PhoneAuthAction.OnCountryChanged -> onCountryChanged(action.country)
            is PhoneAuthAction.OnPhoneNumberChanged -> onPhoneNumberChanged(action.phoneNumber)
            is PhoneAuthAction.OnSendOtpClicked -> attemptSendOtp()
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

    private fun attemptSendOtp() {
        val validated = validatePhoneNumber()
        if (validated.hasPhoneError) {
            setState { validated }
            savedStateHandle[KEY_STATE] = state
            return
        }

        setState { state.copy(isLoading = true) }
        Timber.d("Sending OTP to %s", state.e164PhoneNumber)

        viewModelScope.launch {
            delay(1_500) // 🚧 replace with real API call
            setState { state.copy(isLoading = false) }
            savedStateHandle[KEY_STATE] = state
            sendEvent(PhoneAuthEvent.NavigateToOtp(phoneNumber = state.e164PhoneNumber))
        }
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

// ─── UiCountry ────────────────────────────────────────────────────────────────

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

// ─── State ────────────────────────────────────────────────────────────────────

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

// ─── Events ───────────────────────────────────────────────────────────────────

sealed interface PhoneAuthEvent {
    data class NavigateToOtp(val phoneNumber: String) : PhoneAuthEvent
    data class ShowSnackbar(
        val message: String,
        val detail: String,
        val code: String,
        val type: EchoSnackbarType,
    ) : PhoneAuthEvent
}

// ─── Actions ──────────────────────────────────────────────────────────────────

sealed interface PhoneAuthAction {
    data class OnCountryChanged(val country: UiCountry) : PhoneAuthAction
    data class OnPhoneNumberChanged(val phoneNumber: String) : PhoneAuthAction
    data object OnSendOtpClicked : PhoneAuthAction
}