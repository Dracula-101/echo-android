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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.application.echo.ui.design.theme.EchoTheme

/**
 * A circular progress indicator component for the Echo design system.
 * Simple implementation with only color customization, following shadcn UI approach.
 *
 * @param modifier The modifier to be applied to the progress indicator
 * @param color The color of the progress indicator. Defaults to primary stroke color from theme
 */
@Composable
fun EchoCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = EchoTheme.colorScheme.primary.color,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress_rotation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val strokeWidth = EchoTheme.dimen.divider.large

    Canvas(
        modifier = modifier.size(EchoTheme.dimen.icon.medium)
    ) {
        drawCircularProgressIndicator(
            color = color,
            rotation = rotation,
            strokeWidth = strokeWidth,
        )
    }
}

/**
 * A circular progress indicator with custom progress value and stroke width.
 * Animates smoothly when progress value changes.
 *
 * @param progress The progress value between 0.0 and 1.0
 * @param modifier The modifier to be applied to the progress indicator
 * @param color The color of the progress indicator. Defaults to primary stroke color from theme
 * @param strokeWidth The width of the progress stroke
 * @param size The size of the circular progress indicator
 */
@Composable
fun EchoCircularProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = EchoTheme.colorScheme.primary.color,
    strokeWidth: Dp = EchoTheme.dimen.divider.large,
    size: Dp = EchoTheme.dimen.icon.medium,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress().coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearEasing
        ),
        label = "animated_progress"
    )

    Canvas(
        modifier = modifier.size(size)
    ) {
        drawDeterminateCircularProgressIndicator(
            color = color,
            progress = animatedProgress,
            strokeWidth = strokeWidth
        )
    }
}

private fun DrawScope.drawCircularProgressIndicator(
    color: Color,
    rotation: Float,
    strokeWidth: Dp
) {
    val radius = (size.minDimension - strokeWidth.toPx()) / 2
    val centerX = size.width / 2
    val centerY = size.height / 2

    // Draw the background circle (track)
    drawCircle(
        color = color.copy(alpha = 0.2f),
        radius = radius,
        center = androidx.compose.ui.geometry.Offset(centerX, centerY),
        style = Stroke(
            width = strokeWidth.toPx(),
            cap = StrokeCap.Round
        )
    )

    // Calculate the arc parameters
    val sweepAngle = 90f // Length of the arc
    val startAngle = rotation - 90f // Starting position (adjusted for top start)

    // Draw the progress arc
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(
            width = strokeWidth.toPx(),
            cap = StrokeCap.Round
        ),
        topLeft = androidx.compose.ui.geometry.Offset(
            centerX - radius,
            centerY - radius
        ),
        size = androidx.compose.ui.geometry.Size(
            radius * 2,
            radius * 2
        )
    )
}

private fun DrawScope.drawDeterminateCircularProgressIndicator(
    color: Color,
    progress: Float,
    strokeWidth: Dp
) {
    val strokeWidthPx = strokeWidth.toPx()
    val radius = (size.minDimension - strokeWidthPx) / 2
    val centerX = size.width / 2
    val centerY = size.height / 2

    // Draw the background circle (track)
    drawCircle(
        color = color.copy(alpha = 0.2f),
        radius = radius,
        center = androidx.compose.ui.geometry.Offset(centerX, centerY),
        style = Stroke(
            width = strokeWidthPx,
            cap = StrokeCap.Round
        )
    )

    // Calculate the arc parameters for determinate progress
    val sweepAngle = 360f * progress // Full circle based on progress
    val startAngle = -90f // Start from top (12 o'clock position)

    // Only draw progress arc if there's actual progress
    if (progress > 0f) {
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round
            ),
            topLeft = androidx.compose.ui.geometry.Offset(
                centerX - radius,
                centerY - radius
            ),
            size = androidx.compose.ui.geometry.Size(
                radius * 2,
                radius * 2
            )
        )
    }
}