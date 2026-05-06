package com.application.echo.ui.components.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha10

private const val PILL_ANIM_DURATION_MS = 180

@Composable
fun EchoPill(
    text: String,
    textStyle: TextStyle = EchoTheme.typography.bodyMedium,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            EchoTheme.colorScheme.primary.color.copy(alpha = 0.20f)
        } else {
            EchoTheme.colorScheme.surface.high
        },
        animationSpec = tween(PILL_ANIM_DURATION_MS),
        label = "pill_container_color",
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            EchoTheme.colorScheme.primary.color
        } else {
            EchoTheme.colorScheme.surface.onColor.alpha10
        },
        animationSpec = tween(PILL_ANIM_DURATION_MS),
        label = "pill_border_color",
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) {
            EchoTheme.colorScheme.primary.color
        } else {
            EchoTheme.colorScheme.surface.onColor
        },
        animationSpec = tween(PILL_ANIM_DURATION_MS),
        label = "pill_text_color",
    )

    Surface(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = containerColor,
        border = BorderStroke(
            width = EchoTheme.dimen.border.medium,
            color = borderColor,
        ),
    ) {
        Text(
            text = text,
            style = textStyle,
            color = textColor,
            modifier = Modifier.padding(
                PaddingValues(
                    horizontal = EchoTheme.spacing.padding.medium,
                    vertical = EchoTheme.spacing.padding.small,
                )
            ),
        )
    }
}