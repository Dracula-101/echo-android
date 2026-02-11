package com.application.echo.ui.design.colors

import androidx.compose.ui.graphics.Color

/**
 * Represents secondary colors with their respective variants.
 * Implements ColorShades and ColorAlpha with pre-computed values for performance.
 *
 * @property color The main secondary color.
 * @property onColor The contrasting color for text/icons on the main color.
 * @property container The background color for containers.
 * @property onContainer The contrasting color for text/icons on the container.
 * @property shades Pre-computed shade values for the secondary color.
 * @property alphas Pre-computed alpha values for the secondary color.
 */
data class SecondaryColors(
    val color: Color,
    val onColor: Color,
    val container: Color,
    val onContainer: Color,
)