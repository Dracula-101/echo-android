package com.application.echo.ui.design.colors

import androidx.compose.ui.graphics.Color

/**
 * Represents primary colors with their respective variants.
 * Implements ColorShades and ColorAlpha with pre-computed values for performance.
 *
 * @property color The main primary color.
 * @property onColor The contrasting color for text/icons on the main color.
 * @property container The background color for containers.
 * @property onContainer The contrasting color for text/icons on the container.
 * @property bright The bright variant of the primary color.
 * @property dim The dim variant of the primary color.
 * @property shades Pre-computed shade values for the primary color.
 * @property alphas Pre-computed alpha values for the primary color.
 */
data class PrimaryColors(
    val color: Color,
    val onColor: Color,
    val container: Color,
    val onContainer: Color,
    val bright: Color,
    val dim: Color,
)