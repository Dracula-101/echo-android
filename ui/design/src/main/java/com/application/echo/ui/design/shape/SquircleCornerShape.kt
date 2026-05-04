package com.application.echo.ui.design.shape

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

/**
 * A [CornerBasedShape] with per-corner squircle (superellipse) curves instead
 * of circular arcs. Drop-in replacement wherever [CornerBasedShape] is expected
 * (Button, Card, Surface, clip, border, …).
 *
 * @param curvature Superellipse exponent.
 *   2f = circle  |  4f = iOS-style squircle  |  higher = more rectangular
 */
class SquircleCornerShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize,
    val curvature: Float = 4f,
) : CornerBasedShape(
    topStart = topStart,
    topEnd = topEnd,
    bottomEnd = bottomEnd,
    bottomStart = bottomStart,
) {

    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ): Outline = Outline.Generic(
        buildSquirclePath(size, topStart, topEnd, bottomEnd, bottomStart, curvature)
    )

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize,
    ) = SquircleCornerShape(topStart, topEnd, bottomEnd, bottomStart, curvature)
}

// ─── Factory functions (mirror RoundedCornerShape API) ────────────────────────

fun SquircleCornerShape(corner: CornerSize, curvature: Float = 4f) =
    SquircleCornerShape(corner, corner, corner, corner, curvature)

fun SquircleCornerShape(size: Dp, curvature: Float = 4f) =
    SquircleCornerShape(CornerSize(size), curvature = curvature)

fun SquircleCornerShape(percent: Int, curvature: Float = 4f) =
    SquircleCornerShape(CornerSize(percent), curvature = curvature)

fun SquircleCornerShape(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
    curvature: Float = 4f,
) = SquircleCornerShape(
    topStart = CornerSize(topStart),
    topEnd = CornerSize(topEnd),
    bottomEnd = CornerSize(bottomEnd),
    bottomStart = CornerSize(bottomStart),
    curvature = curvature,
)

fun SquircleCornerShape(
    topStartPercent: Int = 0,
    topEndPercent: Int = 0,
    bottomEndPercent: Int = 0,
    bottomStartPercent: Int = 0,
    curvature: Float = 4f,
) = SquircleCornerShape(
    topStart = CornerSize(topStartPercent),
    topEnd = CornerSize(topEndPercent),
    bottomEnd = CornerSize(bottomEndPercent),
    bottomStart = CornerSize(bottomStartPercent),
    curvature = curvature,
)

// ─── Path builder ─────────────────────────────────────────────────────────────

private fun buildSquirclePath(
    size: Size,
    tlR: Float, trR: Float, brR: Float, blR: Float,
    n: Float,
    steps: Int = 90,
): Path = Path().apply {
    val w = size.width
    val h = size.height

    moveTo(0f, tlR)
    addCornerArc(cx = tlR,      cy = tlR,      r = tlR, n, steps, Math.PI,         3 * Math.PI / 2)
    lineTo(w - trR, 0f)
    addCornerArc(cx = w - trR,  cy = trR,      r = trR, n, steps, 3 * Math.PI / 2, 2 * Math.PI)
    lineTo(w, h - brR)
    addCornerArc(cx = w - brR,  cy = h - brR,  r = brR, n, steps, 0.0,             Math.PI / 2)
    lineTo(blR, h)
    addCornerArc(cx = blR,      cy = h - blR,  r = blR, n, steps, Math.PI / 2,     Math.PI)
    close()
}

/**
 * Appends a quarter superellipse arc centred at ([cx], [cy]) with radius [r].
 *
 *   x = cx + r · |cos t|^(2/n) · sign(cos t)
 *   y = cy + r · |sin t|^(2/n) · sign(sin t)
 */
private fun Path.addCornerArc(
    cx: Float, cy: Float, r: Float,
    n: Float, steps: Int,
    tStart: Double, tEnd: Double,
) {
    if (r <= 0f) return
    val exp = 2f / n
    repeat(steps + 1) { i ->
        val t = tStart + (tEnd - tStart) * i / steps
        val x = cx + r * abs(Math.cos(t).toFloat()).pow(exp) * sign(Math.cos(t).toFloat())
        val y = cy + r * abs(Math.sin(t).toFloat()).pow(exp) * sign(Math.sin(t).toFloat())
        lineTo(x, y)
    }
}