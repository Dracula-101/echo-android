package com.application.echo.ui.components.switch

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.design.colors.EchoColorScheme

internal data class SwitchColors(
    val uncheckedTrack: Color,
    val checkedTrack: Color,
    val uncheckedThumb: Color,
    val checkedThumb: Color,
    val disabledUncheckedTrack: Color,
    val disabledUncheckedThumb: Color,
    val disabledCheckedTrack: Color,
    val disabledCheckedThumb: Color,
)

@Composable
internal fun EchoColorScheme.switchColors(variant: EchoVariant): SwitchColors {
    val accent = variant.color()
    val onAccent = variant.onColor()
    return SwitchColors(
        uncheckedTrack = outline.variant,
        checkedTrack = accent,
        uncheckedThumb = surface.high,
        checkedThumb = onAccent,
        disabledUncheckedTrack = surface.lowest,
        disabledUncheckedThumb = outline.variant,
        disabledCheckedTrack = accent.copy(alpha = 0.3f),
        disabledCheckedThumb = onAccent.copy(alpha = 0.4f),
    )
}
