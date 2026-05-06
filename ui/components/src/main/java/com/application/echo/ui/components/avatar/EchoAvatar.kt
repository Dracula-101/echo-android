package com.application.echo.ui.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Circular avatar with deterministic background tint and initial fallback.
 *
 * Use one of the three overloads: [initials] (text fallback), [icon] (vector / drawable),
 * or [painter] (image source — typically driven by an image loader like Coil).
 *
 * ```kotlin
 * EchoAvatar(initials = "AM")
 * EchoAvatar(initials = "AM", size = AvatarSize.Large, status = AvatarStatus.Online)
 * EchoAvatar(painter = rememberAsyncImagePainter(user.photoUrl), initials = user.initials)
 * ```
 *
 * @param initials 1-2 character text fallback shown when no image is provided. Truncated to 2 chars.
 * @param size One of the discrete [AvatarSize] tokens.
 * @param status Optional presence dot rendered at the bottom-right.
 * @param backgroundColor Override for the tinted background. Defaults to the primary container.
 * @param contentColor Override for the text color. Defaults to the primary on-container color.
 * @param ringWidth Optional outer ring width — used by [EchoAvatarStack] for borders.
 * @param ringColor Color of the outer ring. Defaults to the surface color.
 */
@Composable
fun EchoAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    status: AvatarStatus = AvatarStatus.None,
    backgroundColor: Color = EchoTheme.colorScheme.primary.container,
    contentColor: Color = EchoTheme.colorScheme.primary.onContainer,
    ringWidth: Dp = 0.dp,
    ringColor: Color = EchoTheme.colorScheme.surface.color,
) {
    AvatarShell(
        size = size,
        status = status,
        backgroundColor = backgroundColor,
        ringWidth = ringWidth,
        ringColor = ringColor,
        modifier = modifier,
    ) {
        Text(
            text = initials.take(2).uppercase(),
            color = contentColor,
            fontSize = size.fontSize,
            fontWeight = FontWeight.SemiBold,
            style = EchoTheme.typography.titleMedium,
        )
    }
}

/**
 * Avatar variant that renders an [IconResource] (icon / emoji) instead of initials.
 */
@Composable
fun EchoAvatar(
    icon: IconResource,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    status: AvatarStatus = AvatarStatus.None,
    backgroundColor: Color = EchoTheme.colorScheme.primary.container,
    contentColor: Color = EchoTheme.colorScheme.primary.onContainer,
    ringWidth: Dp = 0.dp,
    ringColor: Color = EchoTheme.colorScheme.surface.color,
) {
    AvatarShell(
        size = size,
        status = status,
        backgroundColor = backgroundColor,
        ringWidth = ringWidth,
        ringColor = ringColor,
        modifier = modifier,
    ) {
        icon.Paint(
            modifier = Modifier.size(size.diameter * IconRatio),
            color = contentColor,
        )
    }
}

/**
 * Avatar variant that renders an image from a [Painter] (e.g., Coil's `rememberAsyncImagePainter`).
 *
 * [fallbackInitials] are rendered while the painter is null/loading or if the painter fails.
 */
@Composable
fun EchoAvatar(
    painter: Painter?,
    fallbackInitials: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    status: AvatarStatus = AvatarStatus.None,
    backgroundColor: Color = EchoTheme.colorScheme.primary.container,
    contentColor: Color = EchoTheme.colorScheme.primary.onContainer,
    ringWidth: Dp = 0.dp,
    ringColor: Color = EchoTheme.colorScheme.surface.color,
) {
    if (painter == null) {
        EchoAvatar(
            initials = fallbackInitials,
            modifier = modifier,
            size = size,
            status = status,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            ringWidth = ringWidth,
            ringColor = ringColor,
        )
        return
    }
    AvatarShell(
        size = size,
        status = status,
        backgroundColor = backgroundColor,
        ringWidth = ringWidth,
        ringColor = ringColor,
        modifier = modifier,
    ) {
        androidx.compose.foundation.Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(size.diameter)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun AvatarShell(
    size: AvatarSize,
    status: AvatarStatus,
    backgroundColor: Color,
    ringWidth: Dp,
    ringColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.size(size.diameter + status.outerPadding(size, ringWidth)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size.diameter)
                .clip(CircleShape)
                .then(if (ringWidth > 0.dp) Modifier.border(ringWidth, ringColor, CircleShape) else Modifier)
                .background(backgroundColor),
            contentAlignment = Alignment.Center,
            content = { content() },
        )
        if (status != AvatarStatus.None) {
            StatusDot(
                size = size,
                color = status.color(),
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}

@Composable
private fun StatusDot(
    size: AvatarSize,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size.statusDotSize)
            .offset(x = size.statusOffset, y = size.statusOffset)
            .clip(CircleShape)
            .background(EchoTheme.colorScheme.surface.color)
            .border(size.statusBorderWidth, EchoTheme.colorScheme.surface.color, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size.statusDotSize - size.statusBorderWidth * 2)
                .clip(CircleShape)
                .background(color),
        )
    }
}

private val AvatarSize.statusOffset: Dp
    get() = when (this) {
        AvatarSize.ExtraSmall -> 1.dp
        AvatarSize.Small -> 1.dp
        AvatarSize.Medium -> 2.dp
        AvatarSize.Large -> 2.dp
        AvatarSize.ExtraLarge -> 3.dp
        AvatarSize.TwoExtraLarge -> 4.dp
    }

private fun AvatarStatus.outerPadding(size: AvatarSize, ringWidth: Dp): Dp =
    if (this == AvatarStatus.None) ringWidth * 2 else size.statusDotSize / 2

private const val IconRatio = 0.5f
