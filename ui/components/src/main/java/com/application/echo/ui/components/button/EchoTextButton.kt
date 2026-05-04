package com.application.echo.ui.components.button

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.components.progress.EchoCircularProgressIndicator
import com.application.echo.ui.design.theme.EchoTheme
import java.nio.file.Files.size

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
    isLoading: Boolean = false,
    variant: EchoVariant = EchoVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
) {
    EchoTextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLoading = isLoading,
        variant = variant,
        size = size,
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
    isLoading: Boolean = false,
    variant: EchoVariant = EchoVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(size.iconGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isLoading) {
                    EchoCircularProgressIndicator(
                        modifier = Modifier.size(size.iconSize),
                        strokeWidth = size.borderWidth,
                        color = variant.color().copy(
                            alpha = if (enabled) 1f else 0.1f
                        ),
                    )
                }
                ProvideTextStyle(
                    size.textStyle(),
                    content = content,
                )
            }
        },
    )
}


@Preview(showBackground = true)
@Composable
private fun EchoTextButtonPreview() {
    EchoTheme(isDarkTheme = true) {
        Surface{
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EchoVariant.entries.forEach { variant->
                    EchoTextButton(
                        text = "${variant.name} Button",
                        onClick = {},
                        isLoading = true,
                        variant = variant,
                    )
                }
            }
        }
    }
}
