package com.application.echo.core.network.model

/**
 * Standard API response metadata.
 *
 * Every response from the Echo backend includes a `meta` object
 * with these fields, regardless of success or failure.
 */
data class Meta(
    val success: Boolean,
    val statusCode: Int,
    val timestamp: String,
)
