package com.application.echo.ui.components.icon

import android.provider.CalendarContract
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.common.containerColor
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.components.common.onContainerColor
import com.application.echo.ui.design.colors.EchoColorScheme

private const val DISABLED_CONTENT_ALPHA = 0.38f
private const val OUTLINED_CONTENT_ALPHA = 0.2f

enum class EchoIconButtonStyle {
    Default,
    Tonal,
    Solid,
    Outline,
}

@Composable
internal fun EchoColorScheme.iconButtonColors(
    variant: EchoVariant,
    style: EchoIconButtonStyle = EchoIconButtonStyle.Default,
): IconButtonColors {
    return when (style) {
        EchoIconButtonStyle.Default -> IconButtonDefaults.iconButtonColors(
            contentColor = surface.onColor,
            disabledContentColor = surface.onColor.copy(alpha = DISABLED_CONTENT_ALPHA),
            containerColor = surface.container,
            disabledContainerColor = surface.container.copy(alpha = DISABLED_CONTENT_ALPHA),
        )
        EchoIconButtonStyle.Tonal -> IconButtonDefaults.iconButtonColors(
            contentColor = variant.onContainerColor(),
            disabledContentColor = variant.onContainerColor().copy(alpha = DISABLED_CONTENT_ALPHA),
            containerColor = variant.containerColor(),
            disabledContainerColor = variant.containerColor().copy(alpha = DISABLED_CONTENT_ALPHA),
        )
        EchoIconButtonStyle.Solid -> IconButtonDefaults.iconButtonColors(
            contentColor = variant.onColor(),
            disabledContentColor = variant.onColor().copy(alpha = DISABLED_CONTENT_ALPHA),
            containerColor = variant.color(),
            disabledContainerColor = variant.color().copy(alpha = DISABLED_CONTENT_ALPHA),
        )
        EchoIconButtonStyle.Outline -> IconButtonDefaults.iconButtonColors(
            contentColor = variant.color(),
            disabledContentColor = variant.color().copy(alpha = DISABLED_CONTENT_ALPHA),
            containerColor = variant.color().copy(alpha = OUTLINED_CONTENT_ALPHA),
            disabledContainerColor = variant.color().copy(alpha = OUTLINED_CONTENT_ALPHA * DISABLED_CONTENT_ALPHA),
        )
    }
}

@Composable
internal fun EchoColorScheme.iconButtonBorder(
    variant: EchoVariant,
    style: EchoIconButtonStyle = EchoIconButtonStyle.Default,
): BorderStroke? {
    return when (style) {
        EchoIconButtonStyle.Outline -> BorderStroke(1.dp, variant.color())
        else -> null
    }
}