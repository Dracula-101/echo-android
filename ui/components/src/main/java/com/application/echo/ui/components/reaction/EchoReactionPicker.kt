package com.application.echo.ui.components.reaction

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Default emoji set surfaced when no [emojis] override is provided.
 * Mirrors the mockup's reaction picker order.
 */
val EchoDefaultReactionEmojis: ImmutableList<String> = persistentListOf(
    "❤️", "😂", "🔥", "😮", "😢", "👍",
)

private val PickerHeight: Dp = 44.dp
private val EmojiCellSize: Dp = 36.dp

/**
 * Horizontal pill of emoji reactions.
 *
 * Tap an emoji to fire [onPick]. The current selection (if any) gets a tinted
 * circular highlight.
 *
 * ```kotlin
 * EchoReactionPicker(
 *     selected = { currentReaction },
 *     onPick = { vm.react(it) },
 * )
 * ```
 *
 * @param emojis Emoji codepoints to render. Defaults to [EchoDefaultReactionEmojis].
 * @param selected Lambda returning the currently picked emoji, or null. Deferred read.
 * @param onPick Called with the tapped emoji.
 */
@Composable
fun EchoReactionPicker(
    selected: () -> String?,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier,
    emojis: ImmutableList<String> = EchoDefaultReactionEmojis,
) {
    val current = selected()
    val container = EchoTheme.colorScheme.surface.high
    val highlight = EchoTheme.colorScheme.primary.color.copy(alpha = 0.15f)

    Row(
        modifier = modifier
            .height(PickerHeight)
            .dropShadow(
                shape = CircleShape,
                shadow = Shadow(
                    radius = 16.dp,
                    spread = 0.dp,
                    color = Color.Black.copy(alpha = 0.18f),
                    offset = DpOffset(0.dp, 4.dp),
                ),
            )
            .clip(CircleShape)
            .background(container)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        emojis.forEach { emoji ->
            val isSelected = emoji == current
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = tween(150),
                label = "reactionScale",
            )
            Box(
                modifier = Modifier
                    .size(EmojiCellSize)
                    .clip(CircleShape)
                    .background(if (isSelected) highlight else Color.Transparent)
                    .clickable(
                        indication = null,
                        interactionSource = remember(emoji) { MutableInteractionSource() },
                        onClick = { onPick(emoji) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = emoji,
                    fontSize = 20.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                )
            }
        }
    }
}
