package com.application.echo.ui.components.selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Tappable card with a leading radio dot, a bold title, and a muted description.
 *
 * Use this for mutually exclusive choices in onboarding / settings flows
 * (e.g. "Everyone / People I follow / Just me").
 *
 * ```kotlin
 * EchoSelectionCard(
 *     title = "Everyone",
 *     description = "Anyone on Echo can find you by username and start a chat.",
 *     selected = { mode == DiscoverMode.Everyone },
 *     onClick = { mode = DiscoverMode.Everyone },
 * )
 * ```
 */
@Composable
fun EchoSelectionCard(
    title: String,
    description: String,
    selected: () -> Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
) {
    val isSelected = selected()
    val accent = variant.color()
    val shape = EchoTheme.shapes.card
    val bgIdle = EchoTheme.colorScheme.surface.container
    val bgActive = accent.copy(alpha = 0.10f)
    val borderIdle = EchoTheme.colorScheme.outline.color.copy(alpha = 0.4f)

    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) bgActive else bgIdle,
        animationSpec = tween(180),
        label = "selBg",
    )
    val animatedBorder by animateColorAsState(
        targetValue = if (isSelected) accent else borderIdle,
        animationSpec = tween(180),
        label = "selBorder",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(animatedBg)
            .border(EchoTheme.dimen.border.small, animatedBorder, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(EchoTheme.spacing.padding.medium),
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.medium),
        verticalAlignment = Alignment.Top,
    ) {
        EchoRadioDot(
            selected = { isSelected },
            variant = variant,
            modifier = Modifier.padding(top = 1.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = title,
                style = EchoTheme.typography.titleSmall,
                color = EchoTheme.colorScheme.surface.onColor,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = description,
                style = EchoTheme.typography.bodySmall,
                color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.7f),
            )
        }
    }
}

/**
 * Visual-only radio dot used inside [EchoSelectionCard]. Driven entirely by [selected];
 * input handling is the parent's responsibility.
 */
@Composable
fun EchoRadioDot(
    selected: () -> Boolean,
    modifier: Modifier = Modifier,
    variant: EchoVariant = EchoVariant.Primary,
    size: Dp = 20.dp,
) {
    val isSelected = selected()
    val accent = variant.color()
    val outline = EchoTheme.colorScheme.outline.color

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accent else outline,
        animationSpec = tween(160),
        label = "radioBorder",
    )
    val dotScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(160),
        label = "radioDot",
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(EchoTheme.dimen.border.medium, borderColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size / 2)
                .graphicsLayer {
                    scaleX = dotScale
                    scaleY = dotScale
                }
                .clip(CircleShape)
                .background(accent),
        )
    }
}
