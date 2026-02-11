package com.application.echo.ui.design.colors

import androidx.compose.ui.graphics.Color


/**
 * Represents scrim colors used in the application.
 * * @property color The primary scrim color.
 * * This color is typically used for overlays or dimming effects.
 */
data class ScrimColors(
    val color: Color,
    val variant: Color,
)