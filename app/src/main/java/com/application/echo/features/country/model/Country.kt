package com.application.echo.features.country.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Country(
    val isoCode: String,
    val name: String,
    val dialCode: String,
    val minDigits: Int,
    val maxDigits: Int,
) : Parcelable {
    val e164Prefix: String get() = "+$dialCode"
    val formattedDialCode: String get() = "+$dialCode"

    fun buildE164(localNumber: String): String =
        "+$dialCode${localNumber.trimStart('0')}"
}