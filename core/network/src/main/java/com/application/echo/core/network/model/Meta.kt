package com.application.echo.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Standard API response metadata.
 *
 * Every response from the Echo backend includes a `meta` object
 * with these fields, regardless of success or failure.
 */
data class Meta(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("timestamp")
    val timestamp: String,
)
