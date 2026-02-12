package com.application.echo.ui.components.progress

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.design.theme.EchoTheme

private const val TRACK_ALPHA = 0.2f
private const val BAR_WIDTH_FRACTION = 0.45f

/**
 * Indeterminate linear progress indicator — sliding bar animation.
 *
 * ```kotlin
 * EchoLinearProgressIndicator()
 * ```
 */
@Composable
fun EchoLinearProgressIndicator(
    modifier: Modifier = Modifier,
    variant: EchoVariant = EchoVariant.Primary,
    color: Color = variant.color(),
    strokeWidth: Dp = EchoTheme.dimen.divider.large,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress_animation")
    val progress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "progress",
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth)
            .clip(EchoTheme.shapes.progressIndicator),
    ) {
        drawTrackBar(color)
        drawIndeterminateBar(color, progress)
    }
}

/**
 * Determinate linear progress indicator — fills from 0 → 100 %.
 *
 * ```kotlin
 * EchoLinearProgressIndicator(progress = { uploadProgress })
 * ```
 */
@Composable
fun EchoLinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    variant: EchoVariant = EchoVariant.Primary,
    color: Color = variant.color(),
    strokeWidth: Dp = EchoTheme.dimen.divider.large,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress().coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "animated_progress",
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth)
            .clip(EchoTheme.shapes.progressIndicator),
    ) {
        drawTrackBar(color)
        if (animatedProgress > 0f) {
            drawRect(
                color = color,
                size = Size(width = size.width * animatedProgress, height = size.height),
            )
        }
    }
}

// ──────────────── Internal Canvas helpers ────────────────

private fun DrawScope.drawTrackBar(color: Color) {
    drawRect(
        color = color.copy(alpha = TRACK_ALPHA),
        size = size,
    )
}

private fun DrawScope.drawIndeterminateBar(color: Color, progress: Float) {
    val barWidth = size.width * BAR_WIDTH_FRACTION
    val totalDistance = size.width + barWidth
    val currentPosition = progress * totalDistance - barWidth

    val visibleStart = currentPosition.coerceAtLeast(0f)
    val visibleEnd = (currentPosition + barWidth).coerceAtMost(size.width)

    if (visibleEnd > visibleStart) {
        drawRect(
            color = color,
            topLeft = Offset(x = visibleStart, y = 0f),
            size = Size(width = visibleEnd - visibleStart, height = size.height),
        )
    }
}