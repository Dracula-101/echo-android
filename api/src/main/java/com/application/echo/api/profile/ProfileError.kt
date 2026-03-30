package com.application.echo.api.profile

import com.application.echo.core.network.model.ApiError
import com.application.echo.core.network.model.ApiValidationErrorField
import com.application.echo.core.network.model.NetworkException
import kotlin.collections.orEmpty


/**
 * Domain-specific error hierarchy for all profile operations.
 *
 * Each subtype maps to a backend error code from the profile service.
 * The [fromApiError] factory converts raw [ApiError] codes into the
 * appropriate sealed subtype, enabling exhaustive `when` handling
 * without string comparisons.
 */
sealed class ProfileError(
    open val code: String,
    open val message: String,
) {

    // ── User ─────────────────────────────────────────────────

    data class UserNotFound(override val message: String) : ProfileError("USER_NOT_FOUND", message)
    data class UserAlreadyExists(override val message: String) : ProfileError("USER_ALREADY_EXISTS", message)
    data class InvalidUserId(override val message: String) : ProfileError("INVALID_USER_ID", message)
    data class InvalidUserData(override val message: String) : ProfileError("INVALID_USER_DATA", message)
    data class UsernameUnavailable(override val message: String) : ProfileError("USERNAME_UNAVAILABLE", message)

    // ── Profile ──────────────────────────────────────────────

    data class ProfileNotFound(override val message: String) : ProfileError("PROFILE_NOT_FOUND", message)
    data class InvalidProfileData(override val message: String) : ProfileError("INVALID_PROFILE_DATA", message)
    data class ProfileUpdateFailed(override val message: String) : ProfileError("PROFILE_UPDATE_FAILED", message)

    // ── Search ───────────────────────────────────────────────

    data class SearchFailed(override val message: String) : ProfileError("SEARCH_FAILED", message)
    data class InvalidSearchQuery(override val message: String) : ProfileError("INVALID_SEARCH_QUERY", message)

    // ── Database ─────────────────────────────────────────────

    data class DatabaseError(override val message: String) : ProfileError("DATABASE_ERROR", message)
    data class DatabaseConnectionError(override val message: String) : ProfileError("DATABASE_CONNECTION_ERROR", message)

    // ── Cache ────────────────────────────────────────────────

    data class CacheError(override val message: String) : ProfileError("CACHE_ERROR", message)

    // ── General ──────────────────────────────────────────────

    data class InternalError(override val message: String) : ProfileError("INTERNAL_ERROR", message)
    data class InvalidRequest(override val message: String) : ProfileError("INVALID_REQUEST", message)
    data class Unauthorized(override val message: String) : ProfileError("UNAUTHORIZED", message)
    data class Forbidden(override val message: String) : ProfileError("FORBIDDEN", message)

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
            // User
            "USER_NOT_FOUND" -> UserNotFound(apiError.message)
            "USER_ALREADY_EXISTS" -> UserAlreadyExists(apiError.message)
            "INVALID_USER_ID" -> InvalidUserId(apiError.message)
            "INVALID_USER_DATA" -> InvalidUserData(apiError.message)
            "USERNAME_UNAVAILABLE" -> UsernameUnavailable(apiError.message)

            // Profile
            "PROFILE_NOT_FOUND" -> ProfileNotFound(apiError.message)
            "INVALID_PROFILE_DATA" -> InvalidProfileData(apiError.message)
            "PROFILE_UPDATE_FAILED" -> ProfileUpdateFailed(apiError.message)

            // Search
            "SEARCH_FAILED" -> SearchFailed(apiError.message)
            "INVALID_SEARCH_QUERY" -> InvalidSearchQuery(apiError.message)

            // Database
            "DATABASE_ERROR" -> DatabaseError(apiError.message)
            "DATABASE_CONNECTION_ERROR" -> DatabaseConnectionError(apiError.message)

            // Cache
            "CACHE_ERROR" -> CacheError(apiError.message)

            // General
            "INTERNAL_ERROR" -> InternalError(apiError.message)
            "INVALID_REQUEST" -> InvalidRequest(apiError.message)
            "UNAUTHORIZED" -> Unauthorized(apiError.message)
            "FORBIDDEN" -> Forbidden(apiError.message)

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