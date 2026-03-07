package com.application.echo.core.network.model

import com.google.gson.annotations.SerializedName

internal data class ApiEnvelope<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?,
    @SerializedName("error") val error: ApiError?,
    @SerializedName("metadata") val metadata: Meta?,
)