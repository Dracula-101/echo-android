package com.application.echo.ui.components.dropdown

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.application.echo.ui.components.modifier.verticalColumnScrollbar
import com.application.echo.ui.design.theme.EchoTheme
import kotlin.math.max
import kotlin.math.min

private const val ANIMATION_DURATION_MS = 120
private const val SCALE_COLLAPSED = 0.8f
private const val DISABLED_ALPHA = 0.3f
private val MAX_MENU_WIDTH = 220.dp
private val MAX_MENU_HEIGHT = 450.dp
private val SCROLLBAR_WIDTH = 4.dp
private val SCROLLBAR_CORNER_RADIUS = 32f
private val LEADING_ICON_SIZE = 20.dp

/**
 * Custom dropdown menu with animated scale/fade entry, themed surface, and a scrollable body.
 *
 * ```kotlin
 * EchoDropdownMenu(
 *     expanded = showMenu,
 *     onDismissRequest = { showMenu = false },
 * ) {
 *     EchoDropdownMenuItem(text = { Text("Edit") }, onClick = ::edit)
 *     EchoDropdownMenuSeparator()
 *     EchoDropdownMenuItem(text = { Text("Delete") }, onClick = ::delete)
 * }
 * ```
 */
@Composable
fun EchoDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    properties: PopupProperties = PopupProperties(focusable = true),
    content: @Composable ColumnScope.() -> Unit,
) {
    val expandedState = remember { MutableTransitionState(false) }
    expandedState.targetState = expanded

    if (expandedState.currentState || expandedState.targetState) {
        val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }
        val density = LocalDensity.current
        val popupPositionProvider = remember(offset, density) {
            DropdownMenuPositionProvider(
                contentOffset = offset,
                density = density,
                onPositionCalculated = { parentBounds, menuBounds ->
                    transformOriginState.value = calculateTransformOrigin(parentBounds, menuBounds)
                },
            )
        }

        Popup(
            onDismissRequest = onDismissRequest,
            popupPositionProvider = popupPositionProvider,
            properties = properties,
        ) {
            DropdownMenuContent(
                expandedState = expandedState,
                transformOriginState = transformOriginState,
                modifier = modifier.widthIn(max = MAX_MENU_WIDTH),
                content = content,
            )
        }
    }
}

@Composable
private fun DropdownMenuContent(
    expandedState: MutableTransitionState<Boolean>,
    transformOriginState: MutableState<TransformOrigin>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val transition = rememberTransition(expandedState, "DropdownMenu")

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION_MS) },
        label = "scale",
    ) { expanded -> if (expanded) 1f else SCALE_COLLAPSED }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION_MS) },
        label = "alpha",
    ) { expanded -> if (expanded) 1f else 0f }

    val scrollState = rememberScrollState()
    val dropdownColors = EchoTheme.colorScheme.dropdownColors()

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                transformOrigin = transformOriginState.value
            }
            .shadow(
                elevation = EchoTheme.dimen.border.medium,
                shape = EchoTheme.shapes.dropdown,
            ),
        shape = EchoTheme.shapes.dropdown,
        color = dropdownColors.background,
        border = BorderStroke(
            width = EchoTheme.dimen.divider.small,
            color = dropdownColors.border,
        ),
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .heightIn(max = MAX_MENU_HEIGHT)
                .padding(end = EchoTheme.spacing.padding.extraSmall)
                .verticalColumnScrollbar(
                    scrollState = scrollState,
                    width = SCROLLBAR_WIDTH,
                    showScrollBarTrack = true,
                    scrollBarTrackColor = dropdownColors.scrollTrack,
                    scrollBarColor = dropdownColors.scrollThumb,
                    scrollBarCornerRadius = SCROLLBAR_CORNER_RADIUS,
                )
                .verticalScroll(scrollState),
            content = content,
            horizontalAlignment = Alignment.Start,
        )
    }
}

// ──────────────── Menu Items ────────────────

/**
 * Single menu item inside an [EchoDropdownMenu].
 *
 * @param text Composable slot for the item label.
 * @param onClick Called when the item is tapped.
 * @param leading Optional leading icon composable.
 * @param trailing Optional trailing composable (e.g. shortcut hint or icon).
 * @param enabled Whether the item is interactive; dimmed when `false`.
 */
@Composable
fun EchoDropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val contentColor = EchoTheme.colorScheme.surface.onColor.let { color ->
        if (enabled) color else color.copy(alpha = DISABLED_ALPHA)
    }

    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
            )
            .fillMaxWidth()
            .padding(
                horizontal = EchoTheme.spacing.padding.small,
                vertical = EchoTheme.spacing.padding.extraSmall,
            )
            .padding(end = EchoTheme.spacing.padding.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.small),
    ) {
        if (leading != null) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Box(
                    modifier = Modifier.size(LEADING_ICON_SIZE),
                    contentAlignment = Alignment.Center,
                ) {
                    leading()
                }
            }
        }

        CompositionLocalProvider(LocalContentColor provides contentColor) {
            ProvideTextStyle(EchoTheme.typography.bodyLarge) {
                Box(modifier = Modifier.weight(1f)) {
                    text()
                }
            }
        }

        if (trailing != null) {
            val trailingColor = EchoTheme.colorScheme.primary.onColor.let { color ->
                if (enabled) color else color.copy(alpha = DISABLED_ALPHA)
            }
            CompositionLocalProvider(LocalContentColor provides trailingColor) {
                trailing()
            }
        }
    }
}

/**
 * Section label displayed above a group of menu items.
 */
@Composable
fun EchoDropdownMenuLabel(text: String) {
    Text(
        text = text,
        style = EchoTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .padding(horizontal = EchoTheme.spacing.padding.small)
            .padding(top = EchoTheme.spacing.padding.small),
    )
}

/**
 * Horizontal divider separating groups within a dropdown.
 */
@Composable
fun EchoDropdownMenuSeparator() {
    HorizontalDivider(
        color = EchoTheme.colorScheme.surface.highest,
        thickness = EchoTheme.dimen.divider.small,
    )
}

/**
 * Trailing shortcut hint text (e.g. "Ctrl+S") for an [EchoDropdownMenuItem].
 */
@Composable
fun EchoDropdownMenuShortcut(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = EchoTheme.typography.titleSmall,
        color = EchoTheme.colorScheme.primary.color,
        modifier = modifier,
    )
}

/**
 * Logical group of dropdown items — adds no visual chrome, only semantic grouping.
 */
@Composable
fun EchoDropdownMenuGroup(
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(content = content)
}

// ──────────────── Position Provider ────────────────

private data class DropdownMenuPositionProvider(
    val contentOffset: DpOffset,
    val density: Density,
    val onPositionCalculated: (IntRect, IntRect) -> Unit = { _, _ -> },
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val contentOffsetX = with(density) { contentOffset.x.roundToPx() }
        val contentOffsetY = with(density) { contentOffset.y.roundToPx() }

        val toRight = anchorBounds.right + contentOffsetX
        val toLeft = anchorBounds.left - popupContentSize.width - contentOffsetX

        val x = when {
            toRight + popupContentSize.width <= windowSize.width -> toRight
            toLeft >= 0 -> toLeft
            else -> anchorBounds.left
        }

        val toBottom = maxOf(anchorBounds.bottom + contentOffsetY, contentOffsetY)
        val toTop = anchorBounds.top - popupContentSize.height - contentOffsetY
        val toCenter = anchorBounds.top - popupContentSize.height / 2

        val y = sequenceOf(toBottom, toTop, toCenter).firstOrNull {
            it >= 0 && it + popupContentSize.height <= windowSize.height
        } ?: toTop

        onPositionCalculated(
            anchorBounds,
            IntRect(x, y, x + popupContentSize.width, y + popupContentSize.height),
        )
        return IntOffset(x, y)
    }
}

private fun calculateTransformOrigin(
    anchorBounds: IntRect,
    menuBounds: IntRect,
): TransformOrigin {
    val pivotX = when {
        menuBounds.left >= anchorBounds.right -> 0f
        menuBounds.right <= anchorBounds.left -> 1f
        menuBounds.width == 0 -> 0f
        else -> {
            val intersectionCenter =
                (max(anchorBounds.left, menuBounds.left) + min(anchorBounds.right, menuBounds.right)) / 2
            (intersectionCenter - menuBounds.left).toFloat() / menuBounds.width
        }
    }
    val pivotY = when {
        menuBounds.top >= anchorBounds.bottom -> 0f
        menuBounds.bottom <= anchorBounds.top -> 1f
        menuBounds.height == 0 -> 0f
        else -> {
            val intersectionCenter =
                (max(anchorBounds.top, menuBounds.top) + min(anchorBounds.bottom, menuBounds.bottom)) / 2
            (intersectionCenter - menuBounds.top).toFloat() / menuBounds.height
        }
    }
    return TransformOrigin(pivotX, pivotY)
}
