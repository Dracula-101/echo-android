package com.application.echo.presentation.register

import android.os.Parcelable
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.application.echo.api.auth.AuthError
import com.application.echo.features.auth.model.onError
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.model.PhoneInfo
import com.application.echo.features.auth.model.fold
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.repository.ProfileRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
) : BaseViewModel<RegisterState, RegisterEvent, RegisterAction>(
    initialState = savedStateHandle[KEY_STATE] ?: RegisterState(),
) {

    init {
        authRepository.authStateFlow
            .onEach { authState ->
                if (authState is AuthState.RegisteringWithPhone) {
                    setState {
                        state.copy(
                            phoneInfo = authState.phoneInfo
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnEmailChanged -> setState {
                state.copy(
                    email = action.value,
                    isValidEmail = Patterns.EMAIL_ADDRESS.matcher(action.value).matches()
                )
            }
            is RegisterAction.OnPasswordChanged -> setState {
                state.copy(
                    password = action.value,
                    passwordStrength = action.value.passwordStrength(),
                )
            }
            is RegisterAction.OnChangePasswordVisibility -> setState {
                state.copy(isPasswordVisible = !state.isPasswordVisible)
            }
            is RegisterAction.OnRegisterClick -> {
                savePreRegistrationInfo()
                registerProfile()
            }
        }
    }


    private fun savePreRegistrationInfo() {
        authRepository.setPreRegistrationInfo(
            email = state.email!!,
            password = state.password!!,
        )
    }


    private fun registerProfile() {
        viewModelScope.launch {
            setState { state.copy(isLoading = true) }
            val registerResult = authRepository.register(
                email = state.email!!,
                password = state.password!!,
                phoneNumber = state.phoneInfo!!.phoneNumber,
                phoneCountryCode = state.phoneInfo!!.country.isoCode,
                acceptTerms = true,
            )
            registerResult.fold(
                onSuccess = { user ->
                },
                onError = { error ->
                    setState { state.copy(isLoading = false) }
                    sendEvent(
                        RegisterEvent.ShowSnackbar(
                            message = "Registration failed",
                            detail = error.message,
                            code = error.code,
                            type = EchoSnackbarType.ERROR,
                        )
                    )
                }
            )
        }
    }

    companion object {
        private const val KEY_STATE = "register_state"
    }
}

@Parcelize
data class RegisterState(
    val phoneInfo: PhoneInfo? = null,
    val email: String? = null,
    val emailError: String? = null,
    val isValidEmail: Boolean = false,
    val password: String? = null,
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength.Empty,
    val isLoading: Boolean = false,
) : Parcelable {
}

sealed interface RegisterEvent {
    data class ShowSnackbar(
        val message: String,
        val detail: String? = null,
        val code: String? = null,
        val type: EchoSnackbarType
    ) : RegisterEvent
}

sealed interface RegisterAction {
    data class OnEmailChanged(val value: String) : RegisterAction
    data class OnPasswordChanged(val value: String) : RegisterAction
    data object OnChangePasswordVisibility : RegisterAction
    data object OnRegisterClick : RegisterAction
}
