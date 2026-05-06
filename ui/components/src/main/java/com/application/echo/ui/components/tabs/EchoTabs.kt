package com.application.echo.ui.components.tabs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.collections.immutable.ImmutableList

/**
 * Single entry in [EchoTabs].
 *
 * @param label Display text.
 * @param badgeText Optional badge rendered on the right of the label (e.g., "3" or "99+").
 */
data class EchoTab(
    val label: String,
    val badgeText: String? = null,
)

private val TabHeight: Dp = 40.dp
private val TabIndicatorHeight: Dp = 2.dp
private val TabHorizontalPadding: Dp = 14.dp

/**
 * Underline-style tab row with an animated indicator that slides between tabs.
 *
 * ```kotlin
 * var selected by remember { mutableIntStateOf(0) }
 * EchoTabs(
 *     tabs = persistentListOf(
 *         EchoTab("Inbox"),
 *         EchoTab("Mentions"),
 *         EchoTab("Requests", badgeText = "3"),
 *     ),
 *     selectedIndex = { selected },
 *     onTabSelected = { selected = it },
 * )
 * ```
 *
 * @param tabs Tabs to render. Order is preserved.
 * @param selectedIndex Lambda returning the active index (deferred read).
 * @param onTabSelected Called with the new index when the user taps a tab.
 * @param variant Color family for the active label, indicator, and badge fill.
 */
@Composable
fun EchoTabs(
    tabs: ImmutableList<EchoTab>,
    selectedIndex: () -> Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    variant: EchoVariant = EchoVariant.Primary,
) {
    if (tabs.isEmpty()) return

    val current = selectedIndex().coerceIn(0, tabs.lastIndex)
    val accent = variant.color()
    val inactive = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.55f)
    val density = LocalDensity.current

    var widths by remember(tabs.size) { mutableStateOf(IntArray(tabs.size)) }

    val indicatorWidth = with(density) { widths.getOrNull(current)?.toDp() ?: 0.dp }
    val indicatorOffsetPx = (0 until current).sumOf { widths.getOrElse(it) { 0 } }
    val indicatorOffset = with(density) { indicatorOffsetPx.toDp() }

    val animatedWidth by animateDpAsState(indicatorWidth, tween(220), label = "tabIndicatorW")
    val animatedOffset by animateDpAsState(indicatorOffset, tween(220), label = "tabIndicatorX")

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.height(TabHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, tab ->
                Box(
                    modifier = Modifier
                        .height(TabHeight)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onTabSelected(index) },
                        )
                        .padding(horizontal = TabHorizontalPadding)
                        .onSizeChanged { measured ->
                            if (widths.getOrNull(index) != measured.width) {
                                widths = widths.copyOf().also { it[index] = measured.width }
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    val labelColor by animateColorAsState(
                        targetValue = if (index == current) accent else inactive,
                        animationSpec = tween(durationMillis = 180),
                        label = "tabLabel",
                    )
                    TabContent(
                        label = tab.label,
                        badgeText = tab.badgeText,
                        labelColor = labelColor,
                        badgeContainer = accent,
                        badgeContent = variant.onColor(),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = animatedOffset)
                .width(animatedWidth)
                .height(TabIndicatorHeight)
                .clip(RoundedCornerShape(50))
                .background(accent),
        )
    }
}

@Composable
private fun TabContent(
    label: String,
    badgeText: String?,
    labelColor: Color,
    badgeContainer: Color,
    badgeContent: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.extraSmall),
    ) {
        Text(
            text = label,
            style = EchoTheme.typography.labelLarge,
            color = labelColor,
        )
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeContainer)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = badgeText,
                    style = EchoTheme.typography.labelSmall,
                    color = badgeContent,
                )
            }
        }
    }
}
