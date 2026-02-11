package com.application.echo.core.api.error

import com.application.echo.core.network.model.ApiError
import com.application.echo.core.network.model.NetworkException

/**
 * UI-friendly error representation.
 *
 * Maps raw [NetworkException] to something a ViewModel can directly
 * show in a Snackbar, Dialog, or inline error state — no network
 * knowledge required in the UI layer.
 */
sealed interface UiError {

    /** A single human-readable message for display. */
    val message: String

    /** The original exception for logging / analytics. */
    val cause: NetworkException

    // ──────────────── Subtypes ────────────────

    /**
     * No internet or server unreachable.
     */
    data class NoConnection(
        override val cause: NetworkException,
    ) : UiError {
        override val message: String = "No internet connection. Check your network and try again."
    }

    /**
     * Request timed out.
     */
    data class Timeout(
        override val cause: NetworkException,
    ) : UiError {
        override val message: String = "Request timed out. Please try again."
    }

    /**
     * 401 — user session expired or invalid.
     */
    data class Unauthorized(
        override val cause: NetworkException,
    ) : UiError {
        override val message: String = "Your session has expired. Please sign in again."
    }

    /**
     * 422 — server-side validation failures.
     *
     * [fieldErrors] maps field names (e.g. "email") to error messages.
     */
    data class Validation(
        override val cause: NetworkException,
        val fieldErrors: Map<String, List<String>>,
    ) : UiError {
        override val message: String = fieldErrors.values
            .flatten()
            .firstOrNull()
            ?: "Please check your input and try again."
    }

    /**
     * Server-side error (5xx).
     */
    data class ServerError(
        override val cause: NetworkException,
    ) : UiError {
        override val message: String = "Something went wrong on our end. Please try again later."
    }

    /**
     * Rate-limited (429).
     */
    data class RateLimited(
        override val cause: NetworkException,
    ) : UiError {
        override val message: String = "Too many requests. Please wait a moment and try again."
    }

    /**
     * Generic HTTP error (4xx excluding 401, 422, 429).
     */
    data class ClientError(
        override val cause: NetworkException,
        override val message: String,
    ) : UiError

    /**
     * Unexpected error.
     */
    data class Unknown(
        override val cause: NetworkException,
    ) : UiError {
        override val message: String = "An unexpected error occurred. Please try again."
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Mapper
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Maps any [NetworkException] to a [UiError] suitable for presentation.
 *
 * ```kotlin
 * result.onFailure { exception ->
 *     val error = exception.toUiError()
 *     _uiState.value = ScreenState.Error(error.message)
 * }
 * ```
 */
fun NetworkException.toUiError(): UiError = when (this) {
    is NetworkException.Network -> UiError.NoConnection(this)
    is NetworkException.Timeout -> UiError.Timeout(this)
    is NetworkException.Http -> toHttpUiError()
    is NetworkException.Serialization -> UiError.Unknown(this)
    is NetworkException.Unknown -> UiError.Unknown(this)
}

private fun NetworkException.Http.toHttpUiError(): UiError = when {
    isAuthenticationError -> UiError.Unauthorized(this)
    isValidationError -> UiError.Validation(
        cause = this,
        fieldErrors = validationErrorsByField.mapValues { (_, errors) ->
            errors.map(ApiError::message)
        },
    )
    isRateLimited -> UiError.RateLimited(this)
    isServerError -> UiError.ServerError(this)
    else -> UiError.ClientError(
        cause = this,
        message = errorMessage,
    )
}
