package com.application.echo.features.country.detector

import android.content.Context
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryDetector @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    // Priority: SIM card → network → locale
    fun detectIso(): String {
        return fromSim() ?: fromNetwork() ?: fromLocale()
    }

    private fun fromSim(): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return tm?.simCountryIso
            ?.uppercase()
            ?.takeIf { it.length == 2 }
    }

    private fun fromNetwork(): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return tm?.networkCountryIso
            ?.uppercase()
            ?.takeIf { it.length == 2 }
    }

    private fun fromLocale(): String =
        Locale.getDefault().country.uppercase().takeIf { it.length == 2 } ?: "US"
}