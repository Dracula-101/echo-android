package com.application.echo.ui.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.components.progress.EchoCircularProgressIndicator
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Medium-emphasis button — a lower-emphasis alternative to [EchoFilledButton].
 *
 * Pick a [style] to vary the visual weight (Filled / Tonal / Soft), a [size]
 * for visual hierarchy (Small / Medium / Large), and pass [leadingIcon] /
 * [trailingIcon] for inline iconography.
 *
 * ```kotlin
 * EchoTonalButton(text = "Continue", onClick = ::next)
 * EchoTonalButton(text = "Reply", style = ButtonStyle.Filled, onClick = ::reply)
 * EchoTonalButton(text = "Decline", style = ButtonStyle.Soft, onClick = ::decline)
 * EchoTonalButton(text = "Delete", variant = EchoVariant.Error, onClick = ::delete)
 * EchoTonalButton(
 *     text = "Send",
 *     leadingIcon = IconResource.Vector(Icons.Default.Send),
 *     size = ButtonSize.Large,
 *     modifier = Modifier.fillMaxWidth(),
 *     onClick = ::send,
 * )
 * ```
 */
@Composable
fun EchoTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    leadingIcon: IconResource? = null,
    trailingIcon: IconResource? = null,
) {
    EchoTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant,
        size = size,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
    ) {
        Text(text = text)
    }
}

/**
 * Slot-based variant of [EchoTonalButton]. Use when the label needs custom
 * composition (e.g., mixed text styles, animated content).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    leadingIcon: IconResource? = null,
    trailingIcon: IconResource? = null,
    content: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = EchoTheme.colorScheme.tonalButtonColors(variant),
        shape = EchoTheme.shapes.button,
        contentPadding = size.contentPadding(),
        content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(size.iconGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingIcon?.Paint(modifier = Modifier.size(size.iconSize))
                ProvideTextStyle(value = size.textStyle(), content = content)
                trailingIcon?.Paint(modifier = Modifier.size(size.iconSize))
            }
        },
    )
}


@Preview
@Composable
private fun EchoFilledButtonPreview() {
    EchoTheme (isDarkTheme = true){
        Column (
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            EchoVariant.entries.forEach { variant ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EchoTonalButton(
                        text = variant.name,
                        variant = variant,
                        onClick = {},
                        modifier = Modifier.weight(1f),
                    )
                    EchoTonalButton(
                        text = variant.name,
                        variant = variant,
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}