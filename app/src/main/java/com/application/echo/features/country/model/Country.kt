package com.application.echo.features.country.model

data class Country(
    val isoCode: String,
    val name: String,
    val dialCode: String,
    val minDigits: Int,
    val maxDigits: Int,
) {
    val e164Prefix: String get() = "+$dialCode"
    val formattedDialCode: String get() = "+$dialCode"

    fun buildE164(localNumber: String): String =
        "+$dialCode${localNumber.trimStart('0')}"
}