package com.application.echo.presentation.create_profile

enum class PasswordStrength {
    Empty,
    Weak,
    Medium,
    Strong;

    override fun toString(): String {
        return when (this) {
            Empty -> "Empty"
            Weak -> "Weak"
            Medium -> "Medium"
            Strong -> "Strong"
        }
    }
}

fun String.passwordStrength(): PasswordStrength {
    if (isBlank()) return PasswordStrength.Empty

    var score = 0

    if (length >= 8) score++
    if (any { it.isLowerCase() }) score++
    if (any { it.isUpperCase() }) score++
    if (any { it.isDigit() }) score++
    if (any { !it.isLetterOrDigit() }) score++

    return when {
        score <= 2 -> PasswordStrength.Weak
        score <= 4 -> PasswordStrength.Medium
        else -> PasswordStrength.Strong
    }
}

fun PasswordStrength.barCount(): Int {
    return when (this) {
        PasswordStrength.Empty -> 0
        PasswordStrength.Weak -> 1
        PasswordStrength.Medium -> 2
        PasswordStrength.Strong -> 4
    }
}