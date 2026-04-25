package com.application.echo.features.country.repository

import com.application.echo.features.country.data.DEFAULT_DIGIT_RULE
import com.application.echo.features.country.data.DIAL_CODES
import com.application.echo.features.country.data.DIGIT_RULES
import com.application.echo.features.country.detector.CountryDetector
import com.application.echo.features.country.model.Country
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepositoryImpl @Inject constructor(
    private val detector: CountryDetector,
) : CountryRepository {

    override val all: List<Country> by lazy { buildCountries() }

    override val default: Country by lazy {
        forIso("US") ?: all.first()
    }

    override fun forIso(isoCode: String): Country? =
        all.firstOrNull { it.isoCode.equals(isoCode, ignoreCase = true) }

    override fun forDialCode(dialCode: String): List<Country> =
        all.filter { it.dialCode == dialCode.removePrefix("+") }

    override fun search(query: String): List<Country> {
        if (query.isBlank()) return all
        val q = query.trim().lowercase()
        return all.filter {
            it.name.lowercase().contains(q) ||
            it.isoCode.lowercase() == q ||
            it.dialCode.contains(q) ||
            it.e164Prefix.contains(q)
        }
    }

    override fun detectCurrentCountry(): Country {
        val iso = detector.detectIso()
        return forIso(iso) ?: default
    }

    private fun buildCountries(): List<Country> =
        DIAL_CODES.mapNotNull { (iso, dialCode) ->
            val rule = DIGIT_RULES[iso.uppercase()] ?: DEFAULT_DIGIT_RULE
            Country(
                isoCode = iso.uppercase(),
                name = Locale("", iso).getDisplayCountry(Locale.getDefault()),
                dialCode = dialCode,
                minDigits = rule.min,
                maxDigits = rule.max,
            )
        }.sortedBy { it.name }
}