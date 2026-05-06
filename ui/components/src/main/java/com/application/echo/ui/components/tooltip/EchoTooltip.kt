package com.application.echo.ui.components.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Visual surface for a tooltip — a small dark pill with white text and a soft shadow.
 *
 * This is intentionally just the bubble. Wrap it in Material's
 * `androidx.compose.material3.TooltipBox` (or a custom `Popup`) to anchor and
 * time it against another composable.
 *
 * ```kotlin
 * TooltipBox(
 *     positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
 *     tooltip = { EchoTooltip(text = "Pinned for everyone") },
 *     state = rememberTooltipState(),
 * ) { … }
 * ```
 */
@Composable
fun EchoTooltip(
    text: String,
    modifier: Modifier = Modifier,
) {
    val shape = EchoTheme.shapes.snackbar
    val container = EchoTheme.colorScheme.inverse.surface
    val content = EchoTheme.colorScheme.inverse.onSurface

    Text(
        text = text,
        style = EchoTheme.typography.labelMedium,
        color = content,
        modifier = modifier
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 12.dp,
                    spread = 0.dp,
                    color = Color.Black.copy(alpha = 0.25f),
                    offset = DpOffset(0.dp, 4.dp),
                ),
            )
            .clip(shape)
            .background(container)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
