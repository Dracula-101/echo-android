package com.application.echo.ui.components.button

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.design.colors.EchoColorScheme

@Composable
internal fun EchoColorScheme.filledButtonColors(
    variant: ButtonVariant = ButtonVariant.Primary,
): ButtonColors {
    return ButtonDefaults.buttonColors(
        containerColor = when (variant) {
            ButtonVariant.Primary -> primary.color
            ButtonVariant.Secondary -> secondary.color
            ButtonVariant.Error -> error.color
        },
        disabledContainerColor = when (variant) {
            ButtonVariant.Primary -> primary.color.copy(alpha = 0.2f)
            ButtonVariant.Secondary -> secondary.color.copy(alpha = 0.2f)
            ButtonVariant.Error -> error.color.copy(alpha = 0.2f)
        },
        contentColor = when (variant) {
            ButtonVariant.Primary -> primary.onColor
            ButtonVariant.Secondary -> secondary.onColor
            ButtonVariant.Error -> error.onColor
        },
        disabledContentColor = when (variant) {
            ButtonVariant.Primary -> primary.color.copy(alpha = 0.4f)
            ButtonVariant.Secondary -> secondary.color.copy(alpha = 0.4f)
            ButtonVariant.Error -> error.color.copy(alpha = 0.4f)
        }
    )
}

@Composable
internal fun EchoColorScheme.outlinedButtonColors(
    variant: ButtonVariant = ButtonVariant.Primary,
): ButtonColors {
    return ButtonDefaults.outlinedButtonColors(
        containerColor = Color.Transparent,
        contentColor = when (variant) {
            ButtonVariant.Primary -> primary.color
            ButtonVariant.Secondary -> secondary.color
            ButtonVariant.Error -> error.color
        },
        disabledContainerColor = Color.Transparent,
        disabledContentColor = when (variant) {
            ButtonVariant.Primary -> primary.color.copy(alpha = 0.4f)
            ButtonVariant.Secondary -> secondary.color.copy(alpha = 0.4f)
            ButtonVariant.Error -> error.color.copy(alpha = 0.4f)
        }
    )
}

@Composable
internal fun EchoColorScheme.outlinedButtonBorderColor(
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,
): Color {
    return when (variant) {
        ButtonVariant.Primary -> if (enabled) primary.color else primary.color.copy(alpha = 0.4f)
        ButtonVariant.Secondary -> if (enabled) secondary.color else secondary.color.copy(alpha = 0.4f)
        ButtonVariant.Error -> if (enabled) error.color else error.color.copy(alpha = 0.4f)
    }
}


@Composable
fun EchoColorScheme.textButtonColors(
    variant: ButtonVariant = ButtonVariant.Primary,
): ButtonColors {
    return ButtonDefaults.textButtonColors(
        containerColor = Color.Transparent,
        contentColor = when (variant) {
            ButtonVariant.Primary -> primary.color
            ButtonVariant.Secondary -> secondary.color
            ButtonVariant.Error -> error.color
        },
        disabledContainerColor = Color.Transparent,
        disabledContentColor = when (variant) {
            ButtonVariant.Primary -> primary.color.copy(alpha = 0.4f)
            ButtonVariant.Secondary -> secondary.color.copy(alpha = 0.4f)
            ButtonVariant.Error -> error.color.copy(alpha = 0.4f)
        }
    )
}

@Composable
fun EchoColorScheme.iconButtonColors(
    variant: ButtonVariant = ButtonVariant.Primary,
): IconButtonColors {
    return IconButtonDefaults.iconButtonColors(
        containerColor = surface.container,
        contentColor = when (variant) {
            ButtonVariant.Primary -> primary.color
            ButtonVariant.Secondary -> secondary.color
            ButtonVariant.Error -> error.color
        },
        disabledContainerColor = surface.low,
        disabledContentColor = when (variant) {
            ButtonVariant.Primary -> primary.color.copy(alpha = 0.4f)
            ButtonVariant.Secondary -> secondary.color.copy(alpha = 0.4f)
            ButtonVariant.Error -> error.color.copy(alpha = 0.4f)
        }
    )
}