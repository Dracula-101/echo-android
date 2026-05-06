package com.application.echo.ui.components.toast

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tiny indeterminate spinner used inside [EchoToast]. Standalone so the toast
 * doesn't pull in the full progress component for one shape.
 */
@Composable
internal fun EchoToastSpinner(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp,
) {
    val transition = rememberInfiniteTransition(label = "toastSpinner")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )

    Canvas(modifier = modifier) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        val inset = strokeWidth.toPx()
        val arcSize = androidx.compose.ui.geometry.Size(
            width = size.width - inset * 2,
            height = size.height - inset * 2,
        )
        val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)
        drawArc(
            color = color.copy(alpha = 0.25f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke,
        )
        drawArc(
            color = color,
            startAngle = rotation,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke,
        )
    }
}
