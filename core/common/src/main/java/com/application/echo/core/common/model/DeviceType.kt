package com.application.echo.core.common.model

/**
 * High-level device form-factor classification.
 */
enum class DeviceType(val label: String) {
    PHONE("Phone"),
    TABLET("Tablet"),
    FOLDABLE("Foldable"),
    TV("TV"),
    WATCH("Watch"),
    AUTOMOTIVE("Automotive"),
    UNKNOWN("Unknown")
}