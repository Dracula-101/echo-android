package com.application.echo.features.country.repository

import com.application.echo.features.country.model.Country

interface CountryRepository {
    val all: List<Country>
    val default: Country
    fun forIso(isoCode: String): Country?
    fun forDialCode(dialCode: String): List<Country>
    fun search(query: String): List<Country>
    fun detectCurrentCountry(): Country
}