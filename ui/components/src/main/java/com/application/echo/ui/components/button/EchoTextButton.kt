package com.application.echo.ui.components.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Text-only button — lowest visual weight, used for tertiary actions.
 *
 * ```kotlin
 * EchoTextButton(text = "Skip", onClick = ::skip)
 * EchoTextButton(text = "Delete", variant = EchoVariant.Error, onClick = ::delete)
 * ```
 */
@Composable
fun EchoTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
) {
    EchoTextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant,
    ) {
        Text(text = text)
    }
}

/**
 * Text-only button with composable content slot.
 *
 * Use the [text] overload when you only need a label.
 */
@Composable
fun EchoTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    content: @Composable () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = EchoTheme.colorScheme.textButtonColors(variant),
        shape = EchoTheme.shapes.button,
        contentPadding = PaddingValues(
            horizontal = EchoTheme.spacing.padding.medium,
            vertical = EchoTheme.spacing.padding.small,
        ),
        content = {
            ProvideTextStyle(
                EchoTheme.typography.bodyLarge,
                content = content,
            )
        },
    )
}