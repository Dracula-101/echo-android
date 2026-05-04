package com.application.echo.ui.components.icon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.*
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

@Composable
fun EchoIconButton(
    icon: IconResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    circle: Boolean = false,
    style: EchoIconButtonStyle = EchoIconButtonStyle.Default,
    variant: EchoVariant = EchoVariant.Primary,
    size: EchoIconButtonSize = EchoIconButtonSize.Medium,
) {
    val shape = if (circle) CircleShape else EchoTheme.shapes.button

    val border = when (style) {
        EchoIconButtonStyle.Outline -> BorderStroke(1.dp, variant.color())
        else -> null
    }

    val shadowModifier = if (enabled && style == EchoIconButtonStyle.Solid) {
        Modifier.dropShadow(
            shape = shape,
            shadow = Shadow(
                radius = 24.dp,
                spread = 0.dp,
                color = variant.color().copy(alpha = 0.18f),
                offset = DpOffset(0.dp, 8.dp),
            ),
        )
    } else Modifier

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .then(shadowModifier)
            .border(border ?: BorderStroke(0.dp, Color.Transparent), shape)
            .size(size.buttonSize),
        shape = shape,
        colors = EchoTheme.colorScheme.iconButtonColors(
            variant = variant,
            style = style
        ),
    ) {
        icon.Paint(
            modifier = Modifier.size(size.iconSize),
        )
    }
}

@Preview
@Composable
fun EchoIconButtonPreview() {
    EchoTheme(isDarkTheme = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Styles",
                style = EchoTheme.typography.headlineSmall,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EchoIconButtonStyle.entries.forEach { style ->
                    EchoIconButton(
                        icon = IconResource.Vector(Icons.AutoMirrored.Outlined.ArrowBack),
                        onClick = {},
                        style = style,
                    )
                }
            }
            Text(
                text = "Sizes",
                style = EchoTheme.typography.headlineSmall,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EchoIconButtonSize.entries.forEach { size ->
                    EchoIconButton(
                        icon = IconResource.Vector(Icons.AutoMirrored.Outlined.ArrowBack),
                        onClick = {},
                        size = size,
                    )
                }
            }
            Text(
                text = "Circle",
                style = EchoTheme.typography.headlineSmall,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EchoIconButton(
                    icon = IconResource.Vector(Icons.AutoMirrored.Outlined.ArrowBack),
                    onClick = {},
                    circle = true,
                )
                EchoIconButton(
                    icon = IconResource.Vector(Icons.AutoMirrored.Outlined.ArrowBack),
                    onClick = {},
                    circle = true,
                    style = EchoIconButtonStyle.Outline,
                )
            }
        }
    }
}