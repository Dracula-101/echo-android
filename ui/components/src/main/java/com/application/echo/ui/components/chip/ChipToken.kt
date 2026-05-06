package com.application.echo.ui.components.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.common.containerColor
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.components.common.onContainerColor

@Immutable
internal data class ChipColors(
    val container: Color,
    val content: Color,
    val border: BorderStroke?,
    val dashed: Boolean,
)

private const val ACTIVE_CONTAINER_ALPHA = 0.85f
private const val DISABLED_ALPHA = 0.4f
private val BorderWidth: Dp = 1.5.dp

@Composable
internal fun chipColorsFor(
    style: ChipStyle,
    variant: EchoVariant,
    enabled: Boolean,
): ChipColors {
    val accent = variant.color()
    val container = variant.containerColor()
    val onContainer = variant.onContainerColor()

    val raw = when (style) {
        ChipStyle.Soft -> ChipColors(
            container = container,
            content = onContainer,
            border = null,
            dashed = false,
        )
        ChipStyle.Active -> ChipColors(
            container = accent.copy(alpha = ACTIVE_CONTAINER_ALPHA),
            content = variant.onColor(),
            border = null,
            dashed = false,
        )
        ChipStyle.Solid -> ChipColors(
            container = accent,
            content = variant.onColor(),
            border = null,
            dashed = false,
        )
        ChipStyle.Outline -> ChipColors(
            container = Color.Transparent,
            content = accent,
            border = BorderStroke(BorderWidth, accent),
            dashed = false,
        )
        ChipStyle.Dashed -> ChipColors(
            container = Color.Transparent,
            content = onContainer,
            border = BorderStroke(BorderWidth, onContainer.copy(alpha = 0.4f)),
            dashed = true,
        )
    }

    return if (enabled) raw else raw.copy(
        container = raw.container.copy(alpha = raw.container.alpha * DISABLED_ALPHA),
        content = raw.content.copy(alpha = DISABLED_ALPHA),
        border = raw.border?.let { BorderStroke(it.width, raw.content.copy(alpha = DISABLED_ALPHA)) },
    )
}
