package com.application.echo.ui.design.colors

import androidx.compose.ui.graphics.Color

/**
 * Represents primary colors with their respective variants.
 *
 * @property color The main primary color.
 * @property onColor The contrasting color for text/icons on the main color.
 * @property container The background color for containers.
 * @property onContainer The contrasting color for text/icons on the container.
 */
data class PrimaryColors(
    val color: Color,
    val onColor: Color,
    val container: Color,
    val onContainer: Color,
    val bright: Color,
    val dim: Color,
)