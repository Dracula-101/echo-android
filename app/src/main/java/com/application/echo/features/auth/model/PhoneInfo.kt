package com.application.echo.features.auth.model

import android.os.Parcelable
import com.application.echo.features.country.model.Country
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class PhoneInfo(
    val phoneNumber: String,
    val country: Country,
    val addedAt: Long = System.currentTimeMillis(),
) : Parcelable {
    fun toDisplayString(): String {
        return "${country.formattedDialCode} ${phoneNumber.replace(country.formattedDialCode, "")}"
    }

    fun isExpired(): Boolean {
        val tenMinutesInMillis = 10 * 60 * 1000
        return System.currentTimeMillis() - addedAt > tenMinutesInMillis
    }

}