package com.application.echo.core.network.model

import com.google.gson.annotations.SerializedName

data class ApiValidationErrorField(
    @SerializedName("code")
    val code: String,
    @SerializedName("field")
    val field: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("constraints")
    val constraints: String? = null,
    @SerializedName("value")
    val value: String? = null,
)