package com.application.echo.core.network.model

import java.io.IOException
import retrofit2.HttpException

/**
 * Comprehensive error hierarchy for network operations.
 *
 * Every failure that can occur during a network call is captured as a
 * sealed subtype — callers never need to catch raw exceptions.
 */
sealed class NetworkException {

    /** The underlying [Throwable] that caused the error. */
    abstract val throwable: Throwable

    // ──────────────── HTTP Error (non-2xx response) ────────────────

    /**
     * The server returned an HTTP error response.
     *
     * Contains parsed API-level errors when available.
     */
    data class Http(
        override val throwable: HttpException,
        val message: String,
        val errors: List<ApiError>? = null,
    ) : NetworkException() {

        /** The HTTP status code. */
        val statusCode: HttpStatusCode get() = HttpStatusCode(throwable.code())

        /** The raw HTTP status code integer. */
        val code: Int get() = throwable.code()

        /** The raw error body as a String, if available. */
        val responseBody: String? by lazy {
            throwable.response()?.errorBody()?.string()
        }

        /** A single display-friendly error message. */
        val errorMessage: String get() = errors?.firstOrNull()?.message ?: message

        /** `true` when the response is a 422 validation error. */
        val isValidationError: Boolean get() = statusCode.isValidationError

        /** `true` when the response is a 401 authentication error. */
        val isAuthenticationError: Boolean get() = statusCode.isUnauthorized

        /** `true` when the response is a 5xx server error. */
        val isServerError: Boolean get() = statusCode.isServerError

        /** `true` when the response is a 429 rate-limit error. */
        val isRateLimited: Boolean get() = statusCode.isTooManyRequests

        /** Groups validation errors by their [ApiError.field]. */
        val validationErrorsByField: Map<String, List<ApiError>>
            get() = errors
                ?.filter { it.field != null }
                ?.groupBy { it.field!! }
                ?: emptyMap()
    }

    // ──────────────── Network / Connectivity ────────────────

    /**
     * A connectivity or socket-level failure (no HTTP response received).
     */
    data class Network(override val throwable: IOException) : NetworkException() {
        val message: String get() = throwable.message ?: "Network connection error"
    }

    // ──────────────── Serialization ────────────────

    /**
     * The response body could not be parsed / deserialized.
     */
    data class Serialization(
        override val throwable: Throwable,
        val body: String? = null,
    ) : NetworkException() {
        val message: String get() = throwable.message ?: "Failed to parse response"
    }

    // ──────────────── Timeout ────────────────

    /**
     * The request timed out (connect, read, or write).
     */
    data class Timeout(override val throwable: Throwable) : NetworkException() {
        val message: String get() = "Request timed out"
    }

    // ──────────────── Unknown ────────────────

    /**
     * Catch-all for unexpected errors.
     */
    data class Unknown(override val throwable: Throwable) : NetworkException() {
        val message: String get() = throwable.message ?: "An unexpected error occurred"
    }
}
