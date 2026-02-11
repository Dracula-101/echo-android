package com.application.echo.ui.components.switch

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.design.colors.EchoColorScheme

data class SwitchColors(
    val track: Color,
    val trackPressed: Color,
    val thumb: Color,
    val thumbPressed: Color,
    val disabledTrack: Color,
    val disabledThumb: Color,
    val disabledCheckedTrack: Color,
    val disabledCheckedThumb: Color
)

@Composable
internal fun EchoColorScheme.switchColors(): SwitchColors {
    return SwitchColors(
        track = outline.variant,
        trackPressed = primary.color,
        thumb = surface.high,
        thumbPressed = primary.onColor,
        disabledTrack = surface.lowest,
        disabledThumb = outline.variant,
        disabledCheckedTrack = primary.onColor.copy(alpha = 0.2f),
        disabledCheckedThumb = primary.color.copy(alpha = 0.2f),
    )
}
