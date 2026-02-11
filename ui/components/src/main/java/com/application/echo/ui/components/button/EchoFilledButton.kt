package com.application.echo.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme


@Composable
fun EchoFilledButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    content: @Composable () -> Unit = {},
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
                content = content
            )
        }
    )
}

@PreviewLightDark
@Composable
fun EchoFilledButtonPreview() {
    EchoTheme {
        Row(
            modifier = Modifier
                .background(EchoTheme.colorScheme.background.color)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ButtonVariant.entries.forEach {
                EchoFilledButton(
                    variant = it,
                ) {
                    Text(it.name)
                }
            }
        }
    }
}