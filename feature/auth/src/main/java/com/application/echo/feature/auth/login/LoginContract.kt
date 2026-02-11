package com.application.echo.feature.auth.login

/**
 * MVI contract for the Login screen.
 *
 * Follows the pattern enforced by [BaseViewModel]: a single sealed hierarchy
 * for State, Event, and Action keeps the entire screen's behaviour in one place.
 */

// ─────────────────────────── State ───────────────────────────

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
) {
    val isFormValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
                && emailError == null && passwordError == null
}

// ─────────────────────────── Event ───────────────────────────

sealed interface LoginEvent {
    /** Login succeeded — the auth state will flip automatically; nothing to navigate. */
    data object LoginSuccess : LoginEvent

    /** Unrecoverable error that warrants a snackbar / toast. */
    data class ShowError(val message: String) : LoginEvent
}

// ─────────────────────────── Action ──────────────────────────

sealed interface LoginAction {
    data class EmailChanged(val email: String) : LoginAction
    data class PasswordChanged(val password: String) : LoginAction
    data object LoginClicked : LoginAction
    data object ErrorDismissed : LoginAction
}
