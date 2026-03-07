package com.application.echo.core.network.model

import com.google.gson.annotations.SerializedName

data class ApiError(
    @SerializedName("code")
    val code: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("fields")
    val fields: List<ApiValidationErrorField>? = null,
    @SerializedName("inner_error")
    val innerError: String? = null,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: String,
)
