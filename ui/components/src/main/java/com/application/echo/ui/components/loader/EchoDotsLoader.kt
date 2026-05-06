package com.application.echo.ui.components.loader

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.design.theme.EchoTheme

private const val CycleDurationMs = 1200
private val DefaultDotSize: Dp = 6.dp
private val DefaultDotGap: Dp = 4.dp

/**
 * Three pulsing dots — used inside typing bubbles, "Sending…" buttons, and
 * standalone loading indicators.
 *
 * ```kotlin
 * EchoDotsLoader()
 * EchoDotsLoader(variant = EchoVariant.Neutral, dotSize = 5.dp)
 * ```
 */
@Composable
fun EchoDotsLoader(
    modifier: Modifier = Modifier,
    variant: EchoVariant = EchoVariant.Primary,
    color: Color? = null,
    dotSize: Dp = DefaultDotSize,
    dotGap: Dp = DefaultDotGap,
) {
    val resolvedColor = color ?: variant.color()
    val transition = rememberInfiniteTransition(label = "dotsLoader")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dotGap),
    ) {
        repeat(3) { index ->
            val alpha by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 0.3f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = CycleDurationMs
                        0.3f at 0 using LinearEasing
                        1.0f at (CycleDurationMs / 4) + (index * 120) using LinearEasing
                        0.3f at (CycleDurationMs / 2) + (index * 120) using LinearEasing
                        0.3f at CycleDurationMs
                    },
                    repeatMode = RepeatMode.Restart,
                ),
                label = "dot$index",
            )
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer { this.alpha = alpha }
                    .clip(CircleShape)
                    .background(resolvedColor),
            )
        }
    }
}
