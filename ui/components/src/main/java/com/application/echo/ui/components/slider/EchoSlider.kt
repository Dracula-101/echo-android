package com.application.echo.ui.components.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.design.theme.EchoTheme

private val TrackHeight: Dp = 6.dp
private val ThumbSize: Dp = 20.dp

/**
 * Continuous slider with draggable thumb and tappable track.
 *
 * ```kotlin
 * var fontSize by remember { mutableFloatStateOf(0.55f) }
 * EchoSlider(
 *     value = { fontSize },
 *     onValueChange = { fontSize = it },
 * )
 * ```
 *
 * @param value Lambda returning the current value in `[0f..1f]` (deferred read for performance).
 * @param onValueChange Called continuously while the user drags.
 * @param onValueChangeFinished Called once the gesture ends — useful for committing the value.
 * @param enabled When false, the slider is dimmed and ignores input.
 * @param variant Color family used for the active track and thumb.
 */
@Composable
fun EchoSlider(
    value: () -> Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
) {
    val density = LocalDensity.current
    val thumbPx = with(density) { ThumbSize.toPx() }

    var widthPx by remember { mutableFloatStateOf(0f) }
    val current = value().coerceIn(0f, 1f)

    val activeColor = variant.color()
    val trackColor = EchoTheme.colorScheme.surface.high
    val alpha = if (enabled) 1f else 0.4f

    fun fractionFromX(x: Float): Float {
        val available = (widthPx - thumbPx).coerceAtLeast(1f)
        return ((x - thumbPx / 2f) / available).coerceIn(0f, 1f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ThumbSize)
            .onSizeChanged { widthPx = it.width.toFloat() }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onTap = { offset ->
                        onValueChange(fractionFromX(offset.x))
                        onValueChangeFinished?.invoke()
                    },
                )
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectDragGestures(
                    onDragEnd = { onValueChangeFinished?.invoke() },
                    onDragCancel = { onValueChangeFinished?.invoke() },
                ) { change, _ ->
                    change.consume()
                    onValueChange(fractionFromX(change.position.x))
                }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        // Track + active fill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TrackHeight)
                .clip(RoundedCornerShape(50))
                .background(trackColor.copy(alpha = alpha)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = current)
                    .height(TrackHeight)
                    .clip(RoundedCornerShape(50))
                    .background(activeColor.copy(alpha = alpha)),
            )
        }

        // Thumb
        val thumbOffsetX = with(density) {
            ((widthPx - thumbPx) * current).coerceAtLeast(0f).toDp()
        }
        Box(
            modifier = Modifier
                .offset(x = thumbOffsetX)
                .size(ThumbSize)
                .dropShadow(
                    shape = CircleShape,
                    shadow = Shadow(
                        radius = 12.dp,
                        spread = 0.dp,
                        color = activeColor.copy(alpha = 0.35f),
                        offset = DpOffset(x = 0.dp, y = 4.dp),
                    ),
                )
                .clip(CircleShape)
                .background(activeColor.copy(alpha = alpha)),
        )
    }
}
