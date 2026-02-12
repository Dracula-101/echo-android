package com.application.echo.ui.components.progress

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.design.theme.EchoTheme

private const val TRACK_ALPHA = 0.2f

/**
 * Indeterminate circular progress indicator — infinite spinning arc.
 *
 * ```kotlin
 * EchoCircularProgressIndicator()
 * EchoCircularProgressIndicator(variant = EchoVariant.Secondary)
 * ```
 */
@Composable
fun EchoCircularProgressIndicator(
    modifier: Modifier = Modifier,
    variant: EchoVariant = EchoVariant.Primary,
    color: Color = variant.color(),
    size: Dp = EchoTheme.dimen.icon.medium,
    strokeWidth: Dp = EchoTheme.dimen.divider.large,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )

    Canvas(modifier = modifier.size(size)) {
        drawTrackCircle(color, strokeWidth)
        drawProgressArc(color, startAngle = rotation - 90f, sweepAngle = 90f, strokeWidth)
    }
}

/**
 * Determinate circular progress indicator — fills from 0 → 100 %.
 *
 * ```kotlin
 * EchoCircularProgressIndicator(progress = { downloadProgress })
 * ```
 */
@Composable
fun EchoCircularProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    variant: EchoVariant = EchoVariant.Primary,
    color: Color = variant.color(),
    size: Dp = EchoTheme.dimen.icon.medium,
    strokeWidth: Dp = EchoTheme.dimen.divider.large,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress().coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "animated_progress",
    )

    Canvas(modifier = modifier.size(size)) {
        drawTrackCircle(color, strokeWidth)
        if (animatedProgress > 0f) {
            drawProgressArc(color, startAngle = -90f, sweepAngle = 360f * animatedProgress, strokeWidth)
        }
    }
}

// ──────────────── Internal Canvas helpers ────────────────

private fun DrawScope.drawTrackCircle(color: Color, strokeWidth: Dp) {
    val strokePx = strokeWidth.toPx()
    val radius = (size.minDimension - strokePx) / 2
    val center = Offset(size.width / 2, size.height / 2)
    drawCircle(
        color = color.copy(alpha = TRACK_ALPHA),
        radius = radius,
        center = center,
        style = Stroke(width = strokePx, cap = StrokeCap.Round),
    )
}

private fun DrawScope.drawProgressArc(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    strokeWidth: Dp,
) {
    val strokePx = strokeWidth.toPx()
    val radius = (size.minDimension - strokePx) / 2
    val center = Offset(size.width / 2, size.height / 2)
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(width = strokePx, cap = StrokeCap.Round),
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
    )
}