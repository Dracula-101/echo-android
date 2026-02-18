package com.application.echo.ui.design.colors

import androidx.compose.ui.graphics.Color

/**
 * Represents surface colors used in the application.
 * Implements ColorShades and ColorAlpha with pre-computed values for performance.
 *
 * @property color The main surface color.
 * @property onColor The contrasting color for text/icons on the surface.
 * @property variant A variant of the surface color.
 * @property container The background color for containers.
 * @property onContainer The contrasting color for text/icons on the container.
 * @property highest The highest elevation surface color.
 * @property high The high elevation surface color.
 * @property low The low elevation surface color.
 * @property lowest The lowest elevation surface color.
 */
data class SurfaceColors(
    val color: Color,
    val onColor: Color,
    val variant: Color,
    val container: Color,
    val onContainer: Color,
    val highest: Color,
    val high: Color,
    val low: Color,
    val lowest: Color,
)