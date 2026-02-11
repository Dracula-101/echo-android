package com.application.echo.core.network.model

/**
 * Represents a single error detail returned by the API.
 *
 * May be a top-level error or a field-level validation error.
 *
 * @property code Machine-readable error code (e.g. "EMAIL_TAKEN", "INVALID_FORMAT").
 * @property field The request field this error relates to, or `null` for general errors.
 * @property message Human-readable description suitable for display.
 * @property detail Optional extended description for debugging.
 */
data class ApiError(
    val code: String,
    val field: String? = null,
    val message: String,
    val detail: String? = null,
)
