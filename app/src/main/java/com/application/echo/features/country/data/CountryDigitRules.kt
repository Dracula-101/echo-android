package com.application.echo.features.country.data

internal data class DigitRule(val min: Int, val max: Int)

internal val DIGIT_RULES: Map<String, DigitRule> = mapOf(
    "US" to DigitRule(10, 10), "CA" to DigitRule(10, 10),
    "GB" to DigitRule(10, 10), "AU" to DigitRule(9,  9),
    "IN" to DigitRule(10, 10), "DE" to DigitRule(10, 11),
    "FR" to DigitRule(9,  9),  "BR" to DigitRule(10, 11),
    "MX" to DigitRule(10, 10), "JP" to DigitRule(10, 11),
    "CN" to DigitRule(11, 11), "RU" to DigitRule(10, 10),
    "NG" to DigitRule(10, 10), "PK" to DigitRule(10, 10),
    "BD" to DigitRule(10, 10), "PH" to DigitRule(10, 10),
    "EG" to DigitRule(10, 10), "KE" to DigitRule(9,  9),
    "GH" to DigitRule(9,  9),  "ZA" to DigitRule(9,  9),
    "AR" to DigitRule(10, 10), "CO" to DigitRule(10, 10),
    "TR" to DigitRule(10, 10), "VN" to DigitRule(9,  10),
    "TH" to DigitRule(9,  9),  "IR" to DigitRule(10, 10),
    "SG" to DigitRule(8,  8),  "MY" to DigitRule(9,  10),
    "NZ" to DigitRule(8,  10), "AE" to DigitRule(9,  9),
    "SA" to DigitRule(9,  9),  "IT" to DigitRule(9,  11),
    "ES" to DigitRule(9,  9),  "PL" to DigitRule(9,  9),
    "UA" to DigitRule(9,  9),  "NL" to DigitRule(9,  9),
    "SE" to DigitRule(7,  13), "NO" to DigitRule(8,  8),
    "CH" to DigitRule(9,  9),  "AT" to DigitRule(4,  13),
    "BE" to DigitRule(8,  9),  "PT" to DigitRule(9,  9),
    "IL" to DigitRule(9,  9),  "HK" to DigitRule(8,  8),
    "ID" to DigitRule(5,  12), "KR" to DigitRule(9,  10),
    "DK" to DigitRule(8,  8),  "FI" to DigitRule(5,  12),
    "GR" to DigitRule(10, 10), "HU" to DigitRule(8,  9),
    "CZ" to DigitRule(9,  9),  "RO" to DigitRule(9,  9),
    "SK" to DigitRule(9,  9),  "HR" to DigitRule(8,  9),
    "BG" to DigitRule(8,  9),  "RS" to DigitRule(8,  9),
    "LT" to DigitRule(8,  8),  "LV" to DigitRule(8,  8),
    "EE" to DigitRule(7,  8),  "SI" to DigitRule(8,  8),
)

internal val DEFAULT_DIGIT_RULE = DigitRule(min = 7, max = 10)