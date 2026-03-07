package com.application.echo.feature.auth.screens.login

import android.os.Parcelable
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.result.fold
import com.application.echo.feature.auth.repository.AuthRepository
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
                        generalError = null,
                    )
                }
            }

            is LoginAction.OnPasswordChanged -> {
                setState {
                    state.copy(
                        password = action.password,
                        passwordError = null,
                        generalError = null,
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

        setState { state.copy(isLoading = true, generalError = null) }
        viewModelScope.launch {
            authRepository.login(
                email = state.email.trim(),
                password = state.password,
            ).fold(
                onSuccess = {
                    setState { state.copy(isLoading = false) }
                    sendEvent(LoginEvent.LoginSuccess)
                },
                onFailure = { exception ->
                    Timber.e(exception.throwable, "Login failed")
                    setState { state.copy(isLoading = false) }
                    when(exception) {
                        is NetworkException.Http -> {
                            setState { state.copy(generalError = exception.error?.code) }
                        }
                        is NetworkException.Network -> {
                            setState { state.copy(generalError = "Network error. Please check your connection.") }
                        }

                        is NetworkException.Serialization -> {
                            setState { state.copy(generalError = "Unexpected response from server. Please try again later.") }
                        }
                        is NetworkException.Timeout -> {
                            setState { state.copy(generalError = "Request timed out. Please try again.") }
                        }
                        is NetworkException.Unknown -> {
                            setState { state.copy(generalError = "An unexpected error occurred. Please try again.") }
                        }
                    }
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
    val password: String = "123456",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
) : Parcelable {
    val hasFieldErrors: Boolean
        get() = emailError != null || passwordError != null
}

sealed interface LoginEvent {
    data object LoginSuccess : LoginEvent
    data class ShowSnackbar(val message: String) : LoginEvent
}

sealed interface LoginAction {
    data class OnEmailChanged(val email: String) : LoginAction
    data class OnPasswordChanged(val password: String) : LoginAction
    data object OnTogglePasswordVisibility : LoginAction
    data object OnLoginClicked : LoginAction
}
