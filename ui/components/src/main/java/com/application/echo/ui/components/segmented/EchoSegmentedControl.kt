package com.application.echo.ui.components.segmented

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.collections.immutable.ImmutableList

private val SegmentHeight: Dp = 40.dp
private val SegmentInset: Dp = 4.dp

/**
 * Pill-shaped segmented control. The active segment is highlighted by a
 * surface-colored pill that slides between segments.
 *
 * ```kotlin
 * var mode by remember { mutableIntStateOf(0) }
 * EchoSegmentedControl(
 *     options = persistentListOf("All", "Unread", "Groups", "Calls"),
 *     selectedIndex = { mode },
 *     onSelected = { mode = it },
 * )
 * ```
 */
@Composable
fun EchoSegmentedControl(
    options: ImmutableList<String>,
    selectedIndex: () -> Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (options.isEmpty()) return

    val current = selectedIndex().coerceIn(0, options.lastIndex)
    val containerColor = EchoTheme.colorScheme.surface.high
    val pillColor = EchoTheme.colorScheme.surface.color
    val activeContent = EchoTheme.colorScheme.surface.onColor
    val inactiveContent = activeContent.copy(alpha = 0.6f)
    val shape = RoundedCornerShape(50)
    val pillShape = RoundedCornerShape(50)

    val density = LocalDensity.current
    var innerWidthPx by remember { mutableIntStateOf(0) }

    val segmentWidth: Dp = with(density) {
        if (options.isEmpty()) 0.dp else (innerWidthPx.toFloat() / options.size).toDp()
    }
    val animatedOffset by animateDpAsState(
        targetValue = segmentWidth * current,
        animationSpec = tween(durationMillis = 220),
        label = "segOffset",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(SegmentHeight)
            .clip(shape)
            .background(containerColor)
            .padding(SegmentInset),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { innerWidthPx = it.width },
        ) {
            if (innerWidthPx > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = animatedOffset)
                        .width(segmentWidth)
                        .fillMaxHeight()
                        .dropShadow(
                            shape = pillShape,
                            shadow = Shadow(
                                radius = 8.dp,
                                spread = 0.dp,
                                color = activeContent.copy(alpha = 0.08f),
                                offset = DpOffset(0.dp, 2.dp),
                            ),
                        )
                        .clip(pillShape)
                        .background(pillColor),
                )
            }

            Row(modifier = Modifier.fillMaxSize()) {
                options.forEachIndexed { index, label ->
                    val color by animateColorAsState(
                        targetValue = if (index == current) activeContent else inactiveContent,
                        animationSpec = tween(180),
                        label = "segLabel",
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(pillShape)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onSelected(index) },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = EchoTheme.typography.labelLarge,
                            color = color,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
