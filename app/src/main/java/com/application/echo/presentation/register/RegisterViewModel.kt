package com.application.echo.presentation.register

import android.os.Parcelable
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.api.auth.AuthError
import com.application.echo.features.auth.model.onError
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
) : BaseViewModel<RegisterState, RegisterEvent, RegisterAction>(
    initialState = savedStateHandle[KEY_STATE] ?: RegisterState(),
) {

    override fun handleAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnEmailChanged -> {
                setState {
                    state.copy(
                        email = action.email,
                        emailError = null,
                    )
                }
            }

            is RegisterAction.OnPasswordChanged -> {
                setState {
                    state.copy(
                        password = action.password,
                        passwordError = null,
                    )
                }
            }

            is RegisterAction.OnConfirmPasswordChanged -> {
                setState {
                    state.copy(
                        confirmPassword = action.confirmPassword,
                        confirmPasswordError = null,
                    )
                }
            }

            RegisterAction.OnTogglePasswordVisibility -> {
                setState { state.copy(isPasswordVisible = !state.isPasswordVisible) }
            }

            RegisterAction.OnToggleConfirmPasswordVisibility -> {
                setState { state.copy(isConfirmPasswordVisible = !state.isConfirmPasswordVisible) }
            }

            RegisterAction.OnRegisterClicked -> attemptRegister()
        }
        savedStateHandle[KEY_STATE] = state
    }

    private fun attemptRegister() {
        val validatedState = validateFields()
        if (validatedState.hasFieldErrors) {
            setState { validatedState }
            savedStateHandle[KEY_STATE] = state
            return
        }

        setState { state.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.register(
                email = state.email.trim(),
                password = state.password,
                acceptTerms = true,
            ).onError { error ->
                setState { state.copy(isLoading = false) }
                val errorMessage = when (error) {
                    is AuthError.EmailExists -> "Use a different email address."
                    is AuthError.InvalidEmail -> "Enter a valid email."
                    is AuthError.WeakPassword -> error.constraints ?: "Password is too weak."
                    is AuthError.TermsNotAccepted -> "You must accept the terms to register."
                    is AuthError.NetworkError -> "Network error"
                    is AuthError.Unknown -> "Unknown error occurred"
                    else -> error.message
                }
                sendEvent(RegisterEvent.ShowSnackbar(
                    message = errorMessage,
                    code = error.code,
                    detail = error.message,
                    type = EchoSnackbarType.ERROR,
                ))
                savedStateHandle[KEY_STATE] = state
            }
        }
    }

    private fun validateFields(): RegisterState {
        val emailError = when {
            state.email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches() -> "Enter a valid email address"
            else -> null
        }
        val passwordError = when {
            state.password.isBlank() -> "Password is required"
            state.password.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }
        val confirmPasswordError = when {
            state.confirmPassword.isBlank() -> "Please confirm your password"
            state.confirmPassword != state.password -> "Passwords do not match"
            else -> null
        }
        return state.copy(
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
        )
    }

    companion object {
        private const val KEY_STATE = "register_state"
    }
}

@Parcelize
data class RegisterState(
    val email: String = "pratikpujari1000@gmail.com",
    val password: String = "12345678",
    val confirmPassword: String = "12345678",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
) : Parcelable {
    val hasFieldErrors: Boolean
        get() = emailError != null || passwordError != null || confirmPasswordError != null
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
    data class OnEmailChanged(val email: String) : RegisterAction
    data class OnPasswordChanged(val password: String) : RegisterAction
    data class OnConfirmPasswordChanged(val confirmPassword: String) : RegisterAction
    data object OnTogglePasswordVisibility : RegisterAction
    data object OnToggleConfirmPasswordVisibility : RegisterAction
    data object OnRegisterClicked : RegisterAction
}
