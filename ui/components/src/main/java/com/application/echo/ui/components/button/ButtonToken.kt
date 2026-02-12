package com.application.echo.ui.components.button

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.design.colors.EchoColorScheme

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Internal token resolvers — used only inside this module.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

private const val DISABLED_CONTAINER_ALPHA = 0.2f
private const val DISABLED_CONTENT_ALPHA = 0.4f

@Composable
internal fun EchoColorScheme.filledButtonColors(
    variant: EchoVariant,
): ButtonColors {
    val accent = variant.color()
    val onAccent = variant.onColor()
    return ButtonDefaults.buttonColors(
        containerColor = accent,
        contentColor = onAccent,
        disabledContainerColor = accent.copy(alpha = DISABLED_CONTAINER_ALPHA),
        disabledContentColor = accent.copy(alpha = DISABLED_CONTENT_ALPHA),
    )
}

@Composable
internal fun EchoColorScheme.outlinedButtonColors(
    variant: EchoVariant,
): ButtonColors {
    val accent = variant.color()
    return ButtonDefaults.outlinedButtonColors(
        containerColor = Color.Transparent,
        contentColor = accent,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = accent.copy(alpha = DISABLED_CONTENT_ALPHA),
    )
}

@Composable
internal fun EchoColorScheme.outlinedButtonBorderColor(
    variant: EchoVariant,
    enabled: Boolean,
): Color {
    val accent = variant.color()
    return if (enabled) accent else accent.copy(alpha = DISABLED_CONTENT_ALPHA)
}

@Composable
internal fun EchoColorScheme.textButtonColors(
    variant: EchoVariant,
): ButtonColors {
    val accent = variant.color()
    return ButtonDefaults.textButtonColors(
        containerColor = Color.Transparent,
        contentColor = accent,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = accent.copy(alpha = DISABLED_CONTENT_ALPHA),
    )
}

@Composable
internal fun EchoColorScheme.iconButtonColors(
    variant: EchoVariant,
): IconButtonColors {
    val accent = variant.color()
    return IconButtonDefaults.iconButtonColors(
        containerColor = surface.container,
        contentColor = accent,
        disabledContainerColor = surface.low,
        disabledContentColor = accent.copy(alpha = DISABLED_CONTENT_ALPHA),
    )
}