package com.application.echo.ui.components.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.echo.ui.components.avatar.AvatarSize
import com.application.echo.ui.components.avatar.EchoAvatar
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

private val ChipHeight: Dp = 28.dp
private val ChipHorizontalPadding: Dp = 12.dp
private val ChipIconSize: Dp = 14.dp
private val ChipRemoveSize: Dp = 14.dp
private val ChipAvatarSize: AvatarSize = AvatarSize.ExtraSmall

/**
 * Compact pill used for filters, status tags, contact selection, and quick actions.
 *
 * ```kotlin
 * EchoChip(text = "Photos")
 * EchoChip(text = "Photos", style = ChipStyle.Active, leadingIcon = IconResource.Vector(Icons.Default.Check))
 * EchoChip(text = "Jules", style = ChipStyle.Soft, leadingAvatar = "J", onRemove = ::dropParticipant)
 * EchoChip(text = "Add filter", style = ChipStyle.Dashed, leadingIcon = IconResource.Vector(Icons.Default.Add))
 * ```
 *
 * @param text Label displayed inside the chip.
 * @param style Visual treatment — see [ChipStyle].
 * @param variant Color family. Defaults to [EchoVariant.Primary].
 * @param leadingIcon Optional icon rendered before the label.
 * @param leadingAvatar Optional initials rendered as a small avatar before the label.
 * @param onClick Optional click callback. Without it the chip is non-interactive.
 * @param onRemove Optional remove callback — adds a trailing close button.
 * @param enabled When false, the chip is dimmed and ignores clicks.
 */
@Composable
fun EchoChip(
    text: String,
    modifier: Modifier = Modifier,
    style: ChipStyle = ChipStyle.Soft,
    variant: EchoVariant = EchoVariant.Primary,
    leadingIcon: IconResource? = null,
    leadingAvatar: String? = null,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    val colors = chipColorsFor(style = style, variant = variant, enabled = enabled)
    val shape = EchoTheme.shapes.chip
    val interactionSource = remember { MutableInteractionSource() }

    val borderModifier = when {
        colors.dashed -> Modifier.dashedBorder(
            color = colors.content.copy(alpha = 0.4f),
            strokeWidth = 1.5.dp,
            cornerRadius = ChipHeight / 2,
        )
        colors.border != null -> Modifier.border(colors.border, shape)
        else -> Modifier
    }

    Row(
        modifier = modifier
            .clip(shape)
            .background(colors.container, shape)
            .then(borderModifier)
            .let {
                if (onClick != null && enabled) {
                    it.clickable(
                        onClick = onClick,
                        indication = null,
                        interactionSource = interactionSource,
                    )
                } else it
            }
            .padding(horizontal = ChipHorizontalPadding, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.extraSmall),
    ) {
        if (leadingAvatar != null) {
            EchoAvatar(
                initials = leadingAvatar,
                size = ChipAvatarSize,
                backgroundColor = colors.content.copy(alpha = 0.18f),
                contentColor = colors.content,
            )
        }
        if (leadingIcon != null) {
            leadingIcon.Paint(
                modifier = Modifier.size(ChipIconSize),
                color = colors.content,
            )
        }
        CompositionLocalProvider(LocalContentColor provides colors.content) {
            ProvideTextStyle(EchoTheme.typography.labelMedium.copy(fontSize = 12.sp)) {
                Text(text = text, color = colors.content)
            }
        }
        if (onRemove != null) {
            Box(
                modifier = Modifier
                    .size(ChipRemoveSize)
                    .clip(CircleShape)
                    .background(colors.content.copy(alpha = 0.25f))
                    .let { if (enabled) it.clickable(onClick = onRemove) else it },
                contentAlignment = Alignment.Center,
            ) {
                IconResource.Vector(Icons.Default.Close).Paint(
                    modifier = Modifier.size(9.dp),
                    color = colors.content,
                )
            }
        }
    }
}

private fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp,
    cornerRadius: Dp,
): Modifier = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f),
    )
    val inset = strokeWidth.toPx() / 2
    drawRoundRect(
        color = color,
        topLeft = Offset(inset, inset),
        size = Size(size.width - inset * 2, size.height - inset * 2),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = stroke,
    )
}
