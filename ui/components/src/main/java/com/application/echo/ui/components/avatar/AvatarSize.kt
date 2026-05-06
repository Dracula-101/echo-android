package com.application.echo.ui.components.avatar

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Discrete avatar sizes used across the app.
 *
 * Sizes follow the design system: 24 / 32 / 44 / 56 / 80 / 104 dp.
 */
enum class AvatarSize(
    val diameter: Dp,
    val fontSize: TextUnit,
    val statusDotSize: Dp,
    val statusBorderWidth: Dp,
) {
    ExtraSmall(diameter = 24.dp, fontSize = 11.sp, statusDotSize = 8.dp, statusBorderWidth = 1.5.dp),
    Small(diameter = 32.dp, fontSize = 13.sp, statusDotSize = 10.dp, statusBorderWidth = 2.dp),
    Medium(diameter = 44.dp, fontSize = 16.sp, statusDotSize = 11.dp, statusBorderWidth = 2.dp),
    Large(diameter = 56.dp, fontSize = 22.sp, statusDotSize = 14.dp, statusBorderWidth = 2.5.dp),
    ExtraLarge(diameter = 80.dp, fontSize = 32.sp, statusDotSize = 18.dp, statusBorderWidth = 3.dp),
    TwoExtraLarge(diameter = 104.dp, fontSize = 42.sp, statusDotSize = 22.dp, statusBorderWidth = 3.dp),
}
