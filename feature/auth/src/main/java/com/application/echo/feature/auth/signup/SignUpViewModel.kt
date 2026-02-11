package com.application.echo.feature.auth.signup

import com.application.echo.core.common.platform.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Sign-Up screen.
 *
 * Currently handles client-side validation only. The actual registration
 * API call will be wired once the endpoint is available.
 */
@HiltViewModel
class SignUpViewModel @Inject constructor() :
    BaseViewModel<SignUpState, SignUpEvent, SignUpAction>(SignUpState()) {

    override fun handleAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.DisplayNameChanged -> handleDisplayNameChanged(action.name)
            is SignUpAction.EmailChanged -> handleEmailChanged(action.email)
            is SignUpAction.PasswordChanged -> handlePasswordChanged(action.password)
            is SignUpAction.ConfirmPasswordChanged -> handleConfirmPasswordChanged(action.confirmPassword)
            is SignUpAction.SignUpClicked -> handleSignUpClicked()
            is SignUpAction.ErrorDismissed -> handleErrorDismissed()
        }
    }

    // ─────────────────────── Action Handlers ─────────────────────

    private fun handleDisplayNameChanged(name: String) {
        setState {
            state.copy(
                displayName = name,
                displayNameError = null,
                generalError = null,
            )
        }
    }

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
                confirmPasswordError = if (state.confirmPassword.isNotEmpty() && state.confirmPassword != password) {
                    "Passwords do not match"
                } else null,
                generalError = null,
            )
        }
    }

    private fun handleConfirmPasswordChanged(confirmPassword: String) {
        setState {
            state.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (confirmPassword != state.password) {
                    "Passwords do not match"
                } else null,
                generalError = null,
            )
        }
    }

    private fun handleSignUpClicked() {
        val current = state

        val nameError = validateDisplayName(current.displayName)
        val emailError = validateEmail(current.email)
        val passwordError = validatePassword(current.password)
        val confirmError = if (current.confirmPassword != current.password) {
            "Passwords do not match"
        } else null

        if (nameError != null || emailError != null || passwordError != null || confirmError != null) {
            setState {
                state.copy(
                    displayNameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmError,
                )
            }
            return
        }

        if (current.isLoading) return

        setState { state.copy(isLoading = true, generalError = null) }

        // TODO: Wire to registration API when available.
        Timber.d("Sign-up submitted for: %s", current.email)
        sendEvent(SignUpEvent.ShowError("Registration is not yet available"))
        setState { state.copy(isLoading = false) }
    }

    private fun handleErrorDismissed() {
        setState { state.copy(generalError = null) }
    }

    // ───────────────────── Validation Helpers ────────────────────

    private fun validateDisplayName(name: String): String? {
        return when {
            name.isBlank() -> "Display name is required"
            name.trim().length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }

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
