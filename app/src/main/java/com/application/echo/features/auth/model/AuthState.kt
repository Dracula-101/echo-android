package com.application.echo.features.auth.model

/**
 * Represents the authentication lifecycle state.
 *
 * Combines [UserState] and token validity into a single observable
 * value so consumers don't need to cross-check multiple sources.
 *
 * ```
 * authRepository.authStateFlow.collect { state ->
 *     when (state) {
 *         is AuthState.Initializing    -> showSplash()
 *         is AuthState.CreateProfile   -> goToCreateProfile(state.userId)
 *         is AuthState.Authenticated   -> goToHome(state.user)
 *         is AuthState.Unauthenticated -> goToLogin()
 *     }
 * }
 * ```
 */
sealed interface AuthState {

    /** App just launched — checking persisted session / refreshing token. */
    data object Initializing : AuthState

    /** User is creating a profile. */
    data class CreateProfile(val userId: String) : AuthState

    /** User is requested OTP verification. */
    data class OtpVerification(val phoneInfo: PhoneInfo) : AuthState

    /** User is logged in and the access token is valid. */
    data class Authenticated(val user: UserState) : AuthState

    /** No session or the session could not be restored. */
    data class Unauthenticated(val reason: Reason = Reason.None) : AuthState {

        enum class Reason {
            /** Fresh state — no prior session. */
            None,
            /** Had a session but the refresh token was rejected. */
            SessionExpired,
            /** Email Verification Required. */
            EmailVerificationRequired,
            /** User explicitly logged out. */
            LoggedOut,
        }
    }
}
