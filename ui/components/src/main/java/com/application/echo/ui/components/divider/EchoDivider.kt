package com.application.echo.ui.components.divider

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme

@Composable
fun EchoDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    spacing: Dp = EchoTheme.spacing.gap.small,
    color: Color = EchoTheme.colorScheme.outline.color,
    orientation: EchoDividerOrientation = EchoDividerOrientation.Horizontal,
) {
    val alphaColor = color.copy(alpha = 0.5f)
    when (orientation) {
        EchoDividerOrientation.Horizontal -> {
            HorizontalDivider(
                modifier = modifier
                    .padding(vertical = spacing)
                    .clip(EchoTheme.shapes.progressIndicator),
                thickness = thickness,
                color = alphaColor
            )
        }

        EchoDividerOrientation.Vertical -> {
            VerticalDivider(
                modifier = modifier
                    .padding(horizontal = spacing)
                    .clip(EchoTheme.shapes.progressIndicator),
                thickness = thickness,
                color = alphaColor
            )
        }
    }
}

enum class EchoDividerOrientation {
    Horizontal,
    Vertical;
}