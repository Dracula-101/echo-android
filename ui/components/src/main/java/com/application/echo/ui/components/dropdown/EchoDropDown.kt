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

@Composable
fun EchoDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    properties: PopupProperties = PopupProperties(focusable = true),
    content: @Composable ColumnScope.() -> Unit
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
                }
            )
        }

        Popup(
            onDismissRequest = onDismissRequest,
            popupPositionProvider = popupPositionProvider,
            properties = properties
        ) {
            DropdownMenuContent(
                expandedState = expandedState,
                transformOriginState = transformOriginState,
                modifier = modifier.widthIn(max = 220.dp),
                content = content
            )
        }
    }
}

@Composable
private fun DropdownMenuContent(
    expandedState: MutableTransitionState<Boolean>,
    transformOriginState: MutableState<TransformOrigin>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val transition = rememberTransition(expandedState, "DropdownMenu")

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 120) },
        label = "scale"
    ) { expanded ->
        if (expanded) 1f else 0.8f
    }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 120) },
        label = "alpha"
    ) { expanded ->
        if (expanded) 1f else 0f
    }

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
                elevation = 2.dp,
                shape = EchoTheme.shapes.dropdown
            ),
        shape = EchoTheme.shapes.dropdown,
        color = dropdownColors.background,
        border = BorderStroke(
            width = 1.dp,
            color = dropdownColors.border
        )
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .heightIn(max = 450.dp)
                .padding(end = 2.dp)
                .verticalColumnScrollbar(
                    scrollState = scrollState,
                    width = 4.dp,
                    showScrollBarTrack = true,
                    scrollBarTrackColor = dropdownColors.scrollTrack,
                    scrollBarColor = dropdownColors.scrollThumb,
                    scrollBarCornerRadius = 32f,
                )
                .verticalScroll(scrollState),
            content = content,
            horizontalAlignment = Alignment.Start
        )
    }
}

@Composable
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
            )
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .padding(end = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (leading != null) {
            CompositionLocalProvider(
                LocalContentColor provides if (enabled) {
                    EchoTheme.colorScheme.surface.onColor
                } else {
                    EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.3f)
                }
            ) {
                Box(
                    modifier = Modifier.size(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    leading()
                }
            }
        }

        CompositionLocalProvider(
            LocalContentColor provides if (enabled) {
                EchoTheme.colorScheme.surface.onColor
            } else {
                EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.3f)
            }
        ) {
            ProvideTextStyle(EchoTheme.typography.bodyLarge) {
                Box(modifier = Modifier.weight(1f)) {
                    text()
                }
            }
        }

        if (trailingIcon != null) {
            CompositionLocalProvider(
                LocalContentColor provides if (enabled) {
                    EchoTheme.colorScheme.primary.onColor
                } else {
                    EchoTheme.colorScheme.primary.onColor.copy(alpha = 0.3f)
                }
            ) {
                trailingIcon()
            }
        }
    }
}

@Composable
fun DropdownMenuLabel(text: String) {
    Text(
        text = text,
        style = EchoTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp),
    )
}

@Composable
fun DropdownMenuSeparator() {
    HorizontalDivider(
        color = EchoTheme.colorScheme.surface.highest,
        thickness = 1.dp
    )
}

@Composable
fun DropdownMenuShortcut(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = EchoTheme.typography.titleSmall,
        color = EchoTheme.colorScheme.primary.color,
        modifier = modifier
    )
}

@Composable
fun DropdownMenuGroup(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(content = content)
}

private data class DropdownMenuPositionProvider(
    val contentOffset: DpOffset,
    val density: Density,
    val onPositionCalculated: (IntRect, IntRect) -> Unit = { _, _ -> }
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
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
            IntRect(x, y, x + popupContentSize.width, y + popupContentSize.height)
        )
        return IntOffset(x, y)
    }
}

private fun calculateTransformOrigin(
    anchorBounds: IntRect,
    menuBounds: IntRect
): TransformOrigin {
    val pivotX = when {
        menuBounds.left >= anchorBounds.right -> 0f
        menuBounds.right <= anchorBounds.left -> 1f
        menuBounds.width == 0 -> 0f
        else -> {
            val intersectionCenter =
                (max(anchorBounds.left, menuBounds.left) + min(
                    anchorBounds.right,
                    menuBounds.right
                )) / 2
            (intersectionCenter - menuBounds.left).toFloat() / menuBounds.width
        }
    }
    val pivotY = when {
        menuBounds.top >= anchorBounds.bottom -> 0f
        menuBounds.bottom <= anchorBounds.top -> 1f
        menuBounds.height == 0 -> 0f
        else -> {
            val intersectionCenter =
                (max(anchorBounds.top, menuBounds.top) + min(
                    anchorBounds.bottom,
                    menuBounds.bottom
                )) / 2
            (intersectionCenter - menuBounds.top).toFloat() / menuBounds.height
        }
    }
    return TransformOrigin(pivotX, pivotY)
}