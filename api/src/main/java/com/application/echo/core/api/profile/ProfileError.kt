package com.application.echo.core.api.profile

import com.application.echo.core.network.model.ApiError
import com.application.echo.core.network.model.ApiValidationErrorField
import com.application.echo.core.network.model.NetworkException
import kotlin.collections.orEmpty


/**
 * Domain-specific error hierarchy for all authentication operations.
 *
 * Each subtype maps to a backend error code from the auth service.
 * The [fromApiError] factory converts raw [ApiError] codes into the
 * appropriate sealed subtype, enabling exhaustive `when` handling
 * without string comparisons.
 */
sealed class ProfileError(
    open val code: String,
    open val message: String,
) {

    // ── Authentication ──────────────────────────────────────────────

    data class InvalidCredentials(
        override val message: String,
        val attemptsRemaining: Int? = null,
    ) : ProfileError("AUTH_INVALID_CREDENTIALS", message)

    data class UserNotFound(
        override val message: String,
    ) : ProfileError("AUTH_USER_NOT_FOUND", message)

    data class AccountLocked(
        override val message: String,
    ) : ProfileError("AUTH_ACCOUNT_LOCKED", message)

    data class AccountDisabled(
        override val message: String,
    ) : ProfileError("AUTH_ACCOUNT_DISABLED", message)

    data class PasswordExpired(
        override val message: String,
    ) : ProfileError("AUTH_PASSWORD_EXPIRED", message)

    data class TwoFactorRequired(
        override val message: String,
    ) : ProfileError("AUTH_2FA_REQUIRED", message)

    data class InvalidTwoFactorCode(
        override val message: String,
    ) : ProfileError("AUTH_INVALID_2FA_CODE", message)

    data class EmailVerificationFailed(
        override val message: String,
    ) : ProfileError("AUTH_EMAIL_VERIFY_FAILED", message)

    data class PhoneVerificationFailed(
        override val message: String,
    ) : ProfileError("AUTH_PHONE_VERIFY_FAILED", message)

    // ── Token ────────────────────────────────────────────────────────

    data class InvalidToken(
        override val message: String,
    ) : ProfileError("AUTH_INVALID_TOKEN", message)

    data class TokenGenerationFailed(
        override val message: String,
    ) : ProfileError("AUTH_TOKEN_GEN_FAILED", message)

    data class TokenValidationFailed(
        override val message: String,
    ) : ProfileError("AUTH_TOKEN_VALIDATE_FAILED", message)

    data class InvalidRefreshToken(
        override val message: String,
    ) : ProfileError("AUTH_INVALID_REFRESH_TOKEN", message)

    data class RefreshTokenExpired(
        override val message: String,
    ) : ProfileError("AUTH_REFRESH_TOKEN_EXPIRED", message)

    // ── Registration ─────────────────────────────────────────────────

    data class EmailExists(
        override val message: String,
        val email: String? = null,
    ) : ProfileError("AUTH_EMAIL_EXISTS", message)

    data class InvalidEmail(
        override val message: String,
    ) : ProfileError("AUTH_INVALID_EMAIL", message)

    data class InvalidPhone(
        override val message: String,
    ) : ProfileError("AUTH_INVALID_PHONE", message)

    data class WeakPassword(
        override val message: String,
        val constraints: String? = null,
    ) : ProfileError("AUTH_PASSWORD_WEAK", message)

    data class TermsNotAccepted(
        override val message: String,
    ) : ProfileError("AUTH_TERMS_NOT_ACCEPTED", message)

    data class PasswordHashFailed(
        override val message: String,
    ) : ProfileError("AUTH_PASSWORD_HASH_FAILED", message)

    // ── Session ──────────────────────────────────────────────────────

    data class SessionNotFound(
        override val message: String,
    ) : ProfileError("AUTH_SESSION_NOT_FOUND", message)

    data class SessionExpired(
        override val message: String,
    ) : ProfileError("AUTH_SESSION_EXPIRED", message)

    data class SessionCreateFailed(
        override val message: String,
    ) : ProfileError("AUTH_SESSION_CREATE_FAILED", message)

    data class SessionUpdateFailed(
        override val message: String,
    ) : ProfileError("AUTH_SESSION_UPDATE_FAILED", message)

    // ── Security ─────────────────────────────────────────────────────

    data class TooManyFailedAttempts(
        override val message: String,
        val retryAfterSeconds: Long? = null,
    ) : ProfileError("AUTH_TOO_MANY_FAILED_ATTEMPTS", message)

    data class SuspiciousActivity(
        override val message: String,
    ) : ProfileError("AUTH_SUSPICIOUS_ACTIVITY", message)

    data class IpBlocked(
        override val message: String,
    ) : ProfileError("AUTH_IP_BLOCKED", message)

    data class DeviceNotTrusted(
        override val message: String,
    ) : ProfileError("AUTH_DEVICE_NOT_TRUSTED", message)

    // ── Database ─────────────────────────────────────────────────────

    data class DatabaseError(
        override val message: String,
    ) : ProfileError("AUTH_DATABASE_ERROR", message)

    // ── Validation (field-level errors from the server) ──────────────

    data class ValidationFailed(
        override val message: String,
        val fields: List<ApiValidationErrorField>,
    ) : ProfileError("VALIDATION_FAILED", message)

    // ── Network / Transport ──────────────────────────────────────────

    /** Connectivity, timeout, or serialization failure — no backend code. */
    data class NetworkError(
        override val message: String,
        val cause: NetworkException,
    ) : ProfileError("NETWORK_ERROR", message)

    // ── Unknown ──────────────────────────────────────────────────────

    /** Fallback for error codes the client doesn't recognize yet. */
    data class Unknown(
        override val code: String,
        override val message: String,
    ) : ProfileError(code, message)

    companion object {

        /**
         * Maps a raw [ApiError] (parsed from an HTTP error body) to a
         * typed [ProfileError]. Falls back to [Unknown] for unrecognized codes.
         */
        fun fromApiError(apiError: ApiError): ProfileError = when (apiError.code) {
            // Authentication
            "AUTH_INVALID_CREDENTIALS" -> InvalidCredentials(apiError.message)
            "AUTH_USER_NOT_FOUND" -> UserNotFound(apiError.message)
            "AUTH_ACCOUNT_LOCKED" -> AccountLocked(apiError.message)
            "AUTH_ACCOUNT_DISABLED" -> AccountDisabled(apiError.message)
            "AUTH_PASSWORD_EXPIRED" -> PasswordExpired(apiError.message)
            "AUTH_2FA_REQUIRED" -> TwoFactorRequired(apiError.message)
            "AUTH_INVALID_2FA_CODE" -> InvalidTwoFactorCode(apiError.message)
            "AUTH_EMAIL_VERIFY_FAILED" -> EmailVerificationFailed(apiError.message)
            "AUTH_PHONE_VERIFY_FAILED" -> PhoneVerificationFailed(apiError.message)

            // Token
            "AUTH_INVALID_TOKEN" -> InvalidToken(apiError.message)
            "AUTH_TOKEN_GEN_FAILED" -> TokenGenerationFailed(apiError.message)
            "AUTH_TOKEN_VALIDATE_FAILED" -> TokenValidationFailed(apiError.message)
            "AUTH_INVALID_REFRESH_TOKEN" -> InvalidRefreshToken(apiError.message)
            "AUTH_REFRESH_TOKEN_EXPIRED" -> RefreshTokenExpired(apiError.message)

            // Registration
            "AUTH_EMAIL_EXISTS" -> EmailExists(apiError.message)
            "AUTH_INVALID_EMAIL" -> InvalidEmail(apiError.message)
            "AUTH_INVALID_PHONE" -> InvalidPhone(apiError.message)
            "AUTH_PASSWORD_WEAK" -> WeakPassword(
                message = apiError.message,
                constraints = apiError.fields?.firstOrNull()?.constraints,
            )
            "AUTH_TERMS_NOT_ACCEPTED" -> TermsNotAccepted(apiError.message)
            "AUTH_PASSWORD_HASH_FAILED" -> PasswordHashFailed(apiError.message)

            // Session
            "AUTH_SESSION_NOT_FOUND" -> SessionNotFound(apiError.message)
            "AUTH_SESSION_EXPIRED" -> SessionExpired(apiError.message)
            "AUTH_SESSION_CREATE_FAILED" -> SessionCreateFailed(apiError.message)
            "AUTH_SESSION_UPDATE_FAILED" -> SessionUpdateFailed(apiError.message)

            // Security
            "AUTH_TOO_MANY_FAILED_ATTEMPTS" -> TooManyFailedAttempts(apiError.message)
            "AUTH_SUSPICIOUS_ACTIVITY" -> SuspiciousActivity(apiError.message)
            "AUTH_IP_BLOCKED" -> IpBlocked(apiError.message)
            "AUTH_DEVICE_NOT_TRUSTED" -> DeviceNotTrusted(apiError.message)

            // Database
            "AUTH_DATABASE_ERROR" -> DatabaseError(apiError.message)

            // Validation (shared code)
            "VALIDATION_FAILED", "VALIDATION_ERROR" -> ValidationFailed(
                message = apiError.message,
                fields = apiError.fields.orEmpty(),
            )

            // Unknown
            else -> Unknown(code = apiError.code, message = apiError.message)
        }

        /**
         * Wraps a [NetworkException] (no backend error code available).
         */
        fun fromNetworkException(exception: NetworkException): ProfileError = when (exception) {
            is NetworkException.Http -> {
                val apiError = exception.error
                if (apiError != null) {
                    fromApiError(apiError)
                } else {
                    Unknown(
                        code = "HTTP_${exception.code}",
                        message = exception.errorMessage,
                    )
                }
            }
            is NetworkException.Network -> NetworkError(exception.message, exception)
            is NetworkException.Timeout -> NetworkError(exception.message, exception)
            is NetworkException.Serialization -> NetworkError(exception.message, exception)
            is NetworkException.Unknown -> NetworkError(exception.message, exception)
        }
    }
}