package com.application.echo.ui.design.dimension

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

@Immutable
data class EchoDimensions(
    val height: Dimensions,
    val width: Dimensions,
    val icon: Dimensions,
    val component: Dimensions,
    val border: Dimensions,
    val divider: Dimensions,
)

@Immutable
data class Dimensions(
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp,
)