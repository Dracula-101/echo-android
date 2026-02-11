package com.application.echo.feature.auth.signup

/**
 * MVI contract for the Sign-Up screen.
 */

// ─────────────────────────── State ───────────────────────────

data class SignUpState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val displayNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
) {
    val isFormValid: Boolean
        get() = displayName.isNotBlank()
                && email.isNotBlank()
                && password.isNotBlank()
                && confirmPassword.isNotBlank()
                && displayNameError == null
                && emailError == null
                && passwordError == null
                && confirmPasswordError == null
}

// ─────────────────────────── Event ───────────────────────────

sealed interface SignUpEvent {
    data object SignUpSuccess : SignUpEvent
    data class ShowError(val message: String) : SignUpEvent
}

// ─────────────────────────── Action ──────────────────────────

sealed interface SignUpAction {
    data class DisplayNameChanged(val name: String) : SignUpAction
    data class EmailChanged(val email: String) : SignUpAction
    data class PasswordChanged(val password: String) : SignUpAction
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignUpAction
    data object SignUpClicked : SignUpAction
    data object ErrorDismissed : SignUpAction
}
