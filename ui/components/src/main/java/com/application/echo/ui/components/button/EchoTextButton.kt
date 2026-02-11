package com.application.echo.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme

@Composable
fun EchoTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = EchoTheme.colorScheme.textButtonColors(),
        shape = EchoTheme.shapes.button,
        contentPadding = PaddingValues(
            horizontal = EchoTheme.spacing.padding.medium,
            vertical = EchoTheme.spacing.padding.small,
        ),
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
private fun PreviewEchoTextButton() {
    EchoTheme {
        FlowRow(
            modifier = Modifier
                .background(EchoTheme.colorScheme.background.color)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EchoTextButton(
                modifier = Modifier.border(
                    1.dp,
                    EchoTheme.colorScheme.outline.color,
                    EchoTheme.shapes.button
                ),
            ) {
                Text("Button")
            }
        }
    }
}