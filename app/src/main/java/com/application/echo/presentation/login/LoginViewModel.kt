package com.application.echo.presentation.login

import android.os.Parcelable
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.core.api.auth.AuthError
import com.application.echo.features.auth.model.fold
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
) : BaseViewModel<LoginState, LoginEvent, LoginAction>(
    initialState = savedStateHandle[KEY_STATE] ?: LoginState(),
) {

    override fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChanged -> {
                setState {
                    state.copy(
                        email = action.email,
                        emailError = null,
                    )
                }
            }

            is LoginAction.OnPasswordChanged -> {
                setState {
                    state.copy(
                        password = action.password,
                        passwordError = null,
                    )
                }
            }

            is LoginAction.OnTogglePasswordVisibility -> {
                setState { state.copy(isPasswordVisible = !state.isPasswordVisible) }
            }

            is LoginAction.OnLoginClicked -> attemptLogin()
        }
        savedStateHandle[KEY_STATE] = state
    }

    private fun attemptLogin() {
        Timber.d("Attempting login with email: %s", state.email)
        val validatedState = validateFields()
        if (validatedState.hasFieldErrors) {
            setState { validatedState }
            savedStateHandle[KEY_STATE] = state
            return
        }

        setState { state.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.login(
                email = state.email.trim(),
                password = state.password,
            ).fold(
                onSuccess = {
                    setState { state.copy(isLoading = false) }
                },
                onError = { error ->
                    Timber.e("Login failed: %s", error.code)
                    setState { state.copy(isLoading = false) }
                    val errorMessage = when (error) {
                        is AuthError.InvalidCredentials -> "Invalid email or password"
                        is AuthError.UserNotFound -> "No account found with this email"
                        is AuthError.AccountLocked -> "Your account has been locked"
                        is AuthError.AccountDisabled -> "Your account has been disabled"
                        is AuthError.TooManyFailedAttempts -> "Too many failed attempts"
                        is AuthError.NetworkError -> "Network error"
                        is AuthError.ValidationFailed -> error.fields.joinToString("\n") { it.message }
                        is AuthError.Unknown -> "Unknown error occurred"
                        else -> "Login failed"
                    }
                    sendEvent(LoginEvent.ShowSnackbar(
                        message = errorMessage,
                        detail = error.message,
                        code = error.code,
                        type = EchoSnackbarType.ERROR,
                    ))
                    savedStateHandle[KEY_STATE] = state
                },
            )
        }
    }

    private fun validateFields(): LoginState {
        val emailError = when {
            state.email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches() -> "Enter a valid email address"
            else -> null
        }
        val passwordError = when {
            state.password.isBlank() -> "Password is required"
            state.password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        return state.copy(emailError = emailError, passwordError = passwordError)
    }

    companion object {
        private const val KEY_STATE = "login_state"
    }
}

@Parcelize
data class LoginState(
    val email: String = "pratikpujari1000@gmail.com",
    val password: String = "12345678",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
) : Parcelable {
    val hasFieldErrors: Boolean
        get() = emailError != null || passwordError != null
}

sealed interface LoginEvent {
    data class ShowSnackbar(val message: String, val detail: String, val code: String, val type: EchoSnackbarType) : LoginEvent
}

sealed interface LoginAction {
    data class OnEmailChanged(val email: String) : LoginAction
    data class OnPasswordChanged(val password: String) : LoginAction
    data object OnTogglePasswordVisibility : LoginAction
    data object OnLoginClicked : LoginAction
}
