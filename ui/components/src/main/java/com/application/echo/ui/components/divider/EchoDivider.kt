package com.application.echo.ui.components.divider

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Themed divider line — horizontal or vertical.
 *
 * ```kotlin
 * EchoDivider()
 * EchoDivider(orientation = EchoDividerOrientation.Vertical)
 * ```
 */
@Composable
fun EchoDivider(
    modifier: Modifier = Modifier,
    orientation: EchoDividerOrientation = EchoDividerOrientation.Horizontal,
    thickness: Dp = EchoTheme.dimen.divider.small,
    spacing: Dp = EchoTheme.spacing.gap.small,
    color: Color = EchoTheme.colorScheme.outline.color.copy(alpha = 0.5f),
) {
    val shape = EchoTheme.shapes.progressIndicator
    when (orientation) {
        EchoDividerOrientation.Horizontal -> HorizontalDivider(
            modifier = modifier.padding(vertical = spacing).clip(shape),
            thickness = thickness,
            color = color,
        )
        EchoDividerOrientation.Vertical -> VerticalDivider(
            modifier = modifier.padding(horizontal = spacing).clip(shape),
            thickness = thickness,
            color = color,
        )
    }
}

enum class EchoDividerOrientation {
    Horizontal,
    Vertical,
}