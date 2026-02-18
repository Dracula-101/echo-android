package com.application.echo.ui.design.colors

import androidx.compose.ui.graphics.Color

/**
 * Represents background colors used in the application.
 * Implements ColorShades with pre-computed values for performance.
 *
 * @property color The main background color.
 * @property onColor The contrasting color for text/icons on the background.
 */
data class BackgroundColors(
    val color: Color,
    val onColor: Color,
)