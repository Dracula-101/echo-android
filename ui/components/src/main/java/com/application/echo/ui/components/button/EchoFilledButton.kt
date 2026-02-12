package com.application.echo.ui.components.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Solid-background button — the primary call-to-action style.
 *
 * ```kotlin
 * EchoFilledButton(text = "Sign In", onClick = ::login)
 * EchoFilledButton(text = "Delete", variant = EchoVariant.Error, onClick = ::delete)
 * ```
 */
@Composable
fun EchoFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
) {
    EchoFilledButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant,
    ) {
        Text(text = text)
    }
}

/**
 * Solid-background button with composable content slot.
 *
 * Use the [text] overload when you only need a label.
 */
@Composable
fun EchoFilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    content: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = EchoTheme.colorScheme.filledButtonColors(variant),
        shape = EchoTheme.shapes.button,
        contentPadding = PaddingValues(
            horizontal = EchoTheme.spacing.padding.medium,
            vertical = EchoTheme.spacing.padding.small,
        ),
        interactionSource = null,
        content = {
            ProvideTextStyle(
                EchoTheme.typography.bodyLarge,
                content = content,
            )
        },
    )
}