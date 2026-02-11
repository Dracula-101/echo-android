package com.application.echo.ui.design.spacing

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

@Immutable
data class EchoSpacing(
    val padding: Spacing,
    val margin: Spacing,
    val gap: Spacing,
    val radius: Spacing,
    val elevation: Spacing,
)


data class Spacing(
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp,
)