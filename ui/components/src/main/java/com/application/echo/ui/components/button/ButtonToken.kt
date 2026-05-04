package com.application.echo.ui.components.button

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.common.containerColor
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.components.common.onContainerColor
import com.application.echo.ui.design.colors.EchoColorScheme

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Internal token resolvers — used only inside this module.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

private const val DISABLED_CONTAINER_ALPHA = 0.2f
private const val DISABLED_CONTENT_ALPHA = 0.4f
private const val BORDER_ALPHA = 0.2f
private const val SOFT_CONTAINER_ALPHA = 0.1f

@Composable
internal fun EchoColorScheme.filledButtonColors(
    variant: EchoVariant,
): ButtonColors {
    val filledColor = variant.color()
    val onFilledColor = when(variant) {
        EchoVariant.Neutral -> surface.color
        else -> variant.onColor()
    }
    return ButtonDefaults.buttonColors(
        containerColor = filledColor,
        contentColor = onFilledColor,
        disabledContainerColor = filledColor.copy(alpha = DISABLED_CONTAINER_ALPHA),
        disabledContentColor = onFilledColor.copy(alpha = DISABLED_CONTENT_ALPHA),
    )
}

@Composable
internal fun EchoColorScheme.tonalButtonColors(
    variant: EchoVariant,
): ButtonColors {
    val container = variant.containerColor()
    val onContainer = variant.onContainerColor()
    return ButtonDefaults.buttonColors(
        containerColor = container,
        contentColor = onContainer,
        disabledContainerColor = container.copy(alpha = DISABLED_CONTAINER_ALPHA),
        disabledContentColor = onContainer.copy(alpha = DISABLED_CONTENT_ALPHA),
    )
}

@Composable
internal fun EchoColorScheme.softButtonColors(
    variant: EchoVariant,
): ButtonColors {
    val accent = variant.color()
    return ButtonDefaults.buttonColors(
        containerColor = accent.copy(alpha = SOFT_CONTAINER_ALPHA),
        contentColor = accent,
        disabledContainerColor = accent.copy(alpha = SOFT_CONTAINER_ALPHA * DISABLED_CONTAINER_ALPHA),
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
    return if (enabled) accent.copy(alpha = BORDER_ALPHA) else accent.copy(alpha = BORDER_ALPHA * DISABLED_CONTAINER_ALPHA)
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