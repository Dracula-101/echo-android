package com.application.echo.ui.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.collections.immutable.ImmutableList

/**
 * One slot in an [EchoAvatarStack]. Pair an [initials] string with a per-avatar
 * background tint so each face stays distinguishable in the stack.
 */
data class AvatarStackItem(
    val initials: String,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val contentColor: androidx.compose.ui.graphics.Color,
)

/**
 * Horizontally overlapping row of avatars, capped by [maxVisible].
 *
 * If there are more avatars than [maxVisible], a final "+N" pill is shown.
 *
 * ```kotlin
 * EchoAvatarStack(
 *     items = persistentListOf(
 *         AvatarStackItem("AM", coralTint, coralBright),
 *         AvatarStackItem("LJ", mintTint, mint),
 *     ),
 *     totalCount = 7,
 *     size = AvatarSize.Small,
 * )
 * ```
 *
 * @param items The avatars to render, in display order. Already trimmed to the desired count.
 * @param totalCount The full group size — used to compute the "+N" overflow label.
 * @param size Common size for every avatar in the stack.
 * @param maxVisible Maximum number of avatars rendered before collapsing to "+N".
 * @param overlap How far each avatar overlaps the previous one. Defaults to one third of [size].
 */
@Composable
fun EchoAvatarStack(
    items: ImmutableList<AvatarStackItem>,
    totalCount: Int,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Small,
    maxVisible: Int = 3,
    overlap: Dp = size.diameter / 3,
) {
    val visible = items.take(maxVisible)
    val remaining = (totalCount - visible.size).coerceAtLeast(0)
    val ringWidth = size.statusBorderWidth
    val ringColor = EchoTheme.colorScheme.surface.color

    Row(modifier = modifier) {
        visible.forEachIndexed { index, item ->
            EchoAvatar(
                initials = item.initials,
                size = size,
                backgroundColor = item.backgroundColor,
                contentColor = item.contentColor,
                ringWidth = ringWidth,
                ringColor = ringColor,
                modifier = Modifier
                    .zIndex((visible.size - index).toFloat())
                    .overlapped(overlap = overlap, index = index),
            )
        }
        if (remaining > 0) {
            Box(
                modifier = Modifier
                    .zIndex(0f)
                    .overlapped(overlap = overlap, index = visible.size)
                    .size(size.diameter)
                    .clip(CircleShape)
                    .border(ringWidth, ringColor, CircleShape)
                    .background(EchoTheme.colorScheme.surface.high),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+$remaining",
                    color = EchoTheme.colorScheme.surface.onColor,
                    fontSize = (size.fontSize.value * 0.85f).sp,
                    fontWeight = FontWeight.SemiBold,
                    style = EchoTheme.typography.labelMedium,
                )
            }
        }
    }
}

/**
 * Reduces the measured width by [overlap] for every item past the first, so the
 * parent [Row] reports the visually-correct combined width while children still
 * draw at their full diameter.
 */
private fun Modifier.overlapped(overlap: Dp, index: Int): Modifier =
    if (index == 0) this else this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val shift = overlap.roundToPx()
        layout(placeable.width - shift, placeable.height) {
            placeable.placeRelative(x = -shift, y = 0)
        }
    }
