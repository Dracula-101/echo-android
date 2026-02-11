package com.application.echo.ui.design.colors

import androidx.compose.ui.graphics.Color

data class Shades(
    /**
     * The lightest shade (95% lightness)
     */
    val shade50: Color,

    /**
     * Light shade (90% lightness)
     */
    val shade100: Color,

    /**
     * Light shade (80% lightness)
     */
    val shade200: Color,

    /**
     * Light shade (70% lightness)
     */
    val shade300: Color,

    /**
     * Medium-light shade (60% lightness)
     */
    val shade400: Color,

    /**
     * Base color (original lightness)
     * */
    val shade500: Color,

    /**
     * Medium-dark shade (40% lightness)
     */
    val shade600: Color,

    /**
     * Dark shade (30% lightness)
     */
    val shade700: Color,

    /**
     * Dark shade (20% lightness)
     */
    val shade800: Color,

    /**
     * Very dark shade (10% lightness)
     */
    val shade900: Color,
)
