package com.application.echo.feature.auth.login

import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Login screen.
 *
 * Manages login form state, input validation, and delegates to
 * [AuthRepository] for the actual authentication call.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
) : BaseViewModel<LoginState, LoginEvent, LoginAction>(LoginState()) {

    override fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> handleEmailChanged(action.email)
            is LoginAction.PasswordChanged -> handlePasswordChanged(action.password)
            is LoginAction.LoginClicked -> handleLoginClicked()
            is LoginAction.ErrorDismissed -> handleErrorDismissed()
        }
    }

    // ─────────────────────── Action Handlers ─────────────────────

    private fun handleEmailChanged(email: String) {
        setState {
            state.copy(
                email = email,
                emailError = null,
                generalError = null,
            )
        }
    }

    private fun handlePasswordChanged(password: String) {
        setState {
            state.copy(
                password = password,
                passwordError = null,
                generalError = null,
            )
        }
    }

    private fun handleLoginClicked() {
        val currentState = state

        // Client-side validation
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)

        if (emailError != null || passwordError != null) {
            setState {
                state.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                )
            }
            return
        }

        // Prevent double-tap
        if (currentState.isLoading) return

        setState { state.copy(isLoading = true, generalError = null) }

        viewModelScope.launch {
        }
    }

    private fun handleErrorDismissed() {
        setState { state.copy(generalError = null) }
    }

    // ───────────────────── Validation Helpers ────────────────────

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !email.trim().contains("@") -> "Enter a valid email address"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }
}
