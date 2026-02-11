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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import com.application.echo.ui.design.theme.EchoTheme

/**
 * A linear progress indicator component for the Echo design system.
 * Simple implementation with only color customization, following shadcn UI approach.
 *
 * @param modifier The modifier to be applied to the progress indicator
 * @param color The color of the progress indicator. Defaults to primary stroke color from theme
 */
@Composable
fun EchoLinearProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = EchoTheme.colorScheme.primary.color,
    strokeWidth: Dp = EchoTheme.dimen.divider.large,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress_animation")

    val progress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth)
            .clip(EchoTheme.shapes.progressIndicator)
    ) {
        drawLinearProgressIndicator(
            color = color,
            progress = progress
        )
    }
}

/**
 * A linear progress indicator with custom progress value and stroke width.
 * Animates smoothly when progress value changes.
 *
 * @param progress The progress value between 0.0 and 1.0
 * @param modifier The modifier to be applied to the progress indicator
 * @param color The color of the progress indicator. Defaults to primary stroke color from theme
 * @param strokeWidth The height/thickness of the progress indicator
 */
@Composable
fun EchoLinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = EchoTheme.colorScheme.primary.color,
    strokeWidth: Dp = EchoTheme.dimen.divider.large,
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
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth)
            .clip(EchoTheme.shapes.progressIndicator)
    ) {
        drawDeterminateLinearProgressIndicator(
            color = color,
            progress = animatedProgress
        )
    }
}

private fun DrawScope.drawLinearProgressIndicator(
    color: Color,
    progress: Float
) {
    val trackHeight = size.height
    val trackWidth = size.width

    // Draw the background track
    drawRect(
        color = color.copy(alpha = 0.2f),
        size = androidx.compose.ui.geometry.Size(
            width = trackWidth,
            height = trackHeight
        )
    )
    val progressBarWidth = trackWidth * 0.45f // 45% of total width
    val totalDistance = trackWidth + progressBarWidth
    val currentPosition = progress * totalDistance - progressBarWidth

    // Only draw if the progress bar is visible within the track
    val visibleStart = currentPosition.coerceAtLeast(0f)
    val visibleEnd = (currentPosition + progressBarWidth).coerceAtMost(trackWidth)

    if (visibleEnd > visibleStart) {
        drawRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(
                x = visibleStart,
                y = 0f
            ),
            size = androidx.compose.ui.geometry.Size(
                width = visibleEnd - visibleStart,
                height = trackHeight
            )
        )
    }
}

private fun DrawScope.drawDeterminateLinearProgressIndicator(
    color: Color,
    progress: Float
) {
    val trackHeight = size.height
    val trackWidth = size.width

    // Draw the background track
    drawRect(
        color = color.copy(alpha = 0.2f),
        size = androidx.compose.ui.geometry.Size(
            width = trackWidth,
            height = trackHeight
        )
    )

    // Calculate progress bar width based on actual progress
    val progressBarWidth = trackWidth * progress

    // Draw the progress bar from start
    if (progressBarWidth > 0f) {
        drawRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(
                x = 0f,
                y = 0f
            ),
            size = androidx.compose.ui.geometry.Size(
                width = progressBarWidth,
                height = trackHeight
            )
        )
    }
}