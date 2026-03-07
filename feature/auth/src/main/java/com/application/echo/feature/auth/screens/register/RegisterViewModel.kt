package com.application.echo.feature.auth.screens.register

import android.os.Parcelable
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.core.network.result.fold
import com.application.echo.feature.auth.repository.AuthRepository
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
                        generalError = null,
                    )
                }
            }

            is RegisterAction.OnPasswordChanged -> {
                setState {
                    state.copy(
                        password = action.password,
                        passwordError = null,
                        generalError = null,
                    )
                }
            }

            is RegisterAction.OnConfirmPasswordChanged -> {
                setState {
                    state.copy(
                        confirmPassword = action.confirmPassword,
                        confirmPasswordError = null,
                        generalError = null,
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

        setState { state.copy(isLoading = true, generalError = null) }
        viewModelScope.launch {
            authRepository.register(
                email = state.email.trim(),
                password = state.password,
                acceptTerms = true,
            ).fold(
                onSuccess = {
                    setState { state.copy(isLoading = false) }
                    sendEvent(RegisterEvent.RegisterSuccess)
                },
                onFailure = { exception ->
                    setState {
                        state.copy(
                            isLoading = false,
                            generalError = exception.throwable.message,
                        )
                    }
                    savedStateHandle[KEY_STATE] = state
                },
            )
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
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
) : Parcelable {
    val hasFieldErrors: Boolean
        get() = emailError != null || passwordError != null || confirmPasswordError != null
}

sealed interface RegisterEvent {
    data object RegisterSuccess : RegisterEvent
    data class ShowSnackbar(val message: String) : RegisterEvent
}

sealed interface RegisterAction {
    data class OnEmailChanged(val email: String) : RegisterAction
    data class OnPasswordChanged(val password: String) : RegisterAction
    data class OnConfirmPasswordChanged(val confirmPassword: String) : RegisterAction
    data object OnTogglePasswordVisibility : RegisterAction
    data object OnToggleConfirmPasswordVisibility : RegisterAction
    data object OnRegisterClicked : RegisterAction
}
