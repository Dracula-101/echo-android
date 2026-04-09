package com.application.echo.core.api.common

import com.google.gson.annotations.SerializedName

/**
 * Response `data` for all `/health` endpoints.
 *
 * Backends return a standard health-check object. Fields are nullable
 * since different services may include different metadata.
 */
data class HealthResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("service")
    val service: String? = null,
    @SerializedName("version")
    val version: String? = null,
    @SerializedName("uptime")
    val uptime: Long? = null,
)
