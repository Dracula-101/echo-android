package com.application.echo.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.progress.EchoCircularProgressIndicator
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Soft button — subtle container with colored content.
 *
 * Good for secondary actions that need more presence than outlined/ghost,
 * but less emphasis than filled.
 */
@Composable
fun EchoSoftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    isLoading: Boolean = false,
    leadingIcon: IconResource? = null,
    trailingIcon: IconResource? = null,
) {
    EchoSoftButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant,
        size = size,
        isLoading = isLoading,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
    ) {
        Text(text = text)
    }
}

@Composable
fun EchoSoftButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    isLoading: Boolean = false,
    leadingIcon: IconResource? = null,
    trailingIcon: IconResource? = null,
    content: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = EchoTheme.colorScheme.softButtonColors(variant),
        shape = EchoTheme.shapes.button,
        contentPadding = size.contentPadding(),
        border = BorderStroke(
            width = size.borderWidth,
            color = EchoTheme.colorScheme.outlinedButtonBorderColor(variant, enabled),
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(size.iconGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.Paint(modifier = Modifier.size(size.iconSize))

            if (isLoading) {
                EchoCircularProgressIndicator(
                    modifier = Modifier.size(size.iconSize),
                    strokeWidth = EchoTheme.dimen.border.medium,
                    color = variant.color().copy(
                        alpha = if (enabled) 1f else 0.38f
                    ),
                )
            }

            ProvideTextStyle(
                value = size.textStyle(),
                content = content,
            )

            trailingIcon?.Paint(modifier = Modifier.size(size.iconSize))
        }
    }
}

@Preview
@Composable
private fun EchoSoftButtonPreview() {
    EchoTheme(isDarkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ButtonSize.entries.forEach { size ->
                Text(
                    text = size.name,
                    style = size.textStyle(),
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                EchoVariant.entries.forEach { variant ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        EchoSoftButton(
                            text = variant.name,
                            variant = variant,
                            onClick = {},
                            isLoading = true,
                            size = size,
                            modifier = Modifier.weight(1f),
                        )

                        EchoSoftButton(
                            text = variant.name,
                            variant = variant,
                            onClick = {},
                            enabled = false,
                            isLoading = true,
                            size = size,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}