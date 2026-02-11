package com.application.echo.ui.design.colors

import androidx.compose.ui.graphics.Color

/**
 * Represents the inverse colors used in the application.
 *
 * @property primary The primary color used in the inverse theme.
 * @property onPrimary The contrasting color for text/icons on the primary color.
 * @property secondary The secondary color used in the inverse theme.
 * @property onSecondary The contrasting color for text/icons on the secondary color.
 * @property surface The surface color used in the inverse theme.
 * @property onSurface The contrasting color for text/icons on the surface color.
 * @property background The background color used in the inverse theme.
 * @property onBackground The contrasting color for text/icons on the background color.
 *
 */
data class InverseColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val surface: Color,
    val onSurface: Color,
    val background: Color,
    val onBackground: Color,
)