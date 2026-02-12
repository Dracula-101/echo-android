package com.application.echo.ui.components.checkbox

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Custom Canvas-based checkbox with an animated checkmark stroke.
 *
 * ```kotlin
 * var agreed by remember { mutableStateOf(false) }
 * EchoCheckbox(
 *     checked = { agreed },
 *     onCheckedChange = { agreed = it },
 * )
 * ```
 *
 * @param checked Lambda returning the current checked state (deferred read for performance).
 * @param onCheckedChange Called when the user taps the checkbox.
 * @param size Visual size — [CheckboxSize.Small], [CheckboxSize.Medium], or [CheckboxSize.Large].
 * @param variant Color variant used for the checked state.
 */
@Composable
fun EchoCheckbox(
    checked: () -> Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: CheckboxSize = CheckboxSize.Medium,
    variant: EchoVariant = EchoVariant.Primary,
) {
    val isChecked = checked()

    val dims = when (size) {
        CheckboxSize.Small -> CheckboxDimensions(16.dp, 44.dp, 4.dp, 1.5.dp, 12.dp)
        CheckboxSize.Medium -> CheckboxDimensions(20.dp, 44.dp, 5.dp, 1.5.dp, 16.dp)
        CheckboxSize.Large -> CheckboxDimensions(24.dp, 48.dp, 6.dp, 2.dp, 18.dp)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isChecked) 1f else 0f,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "checkboxAnimation",
    )

    val accent = variant.color()
    val onAccent = variant.onColor()
    val outline = EchoTheme.colorScheme.outline.variant

    val boxColor: Color
    val borderColor: Color
    val checkColor: Color

    when {
        !enabled -> {
            boxColor = accent.copy(alpha = 0.1f)
            borderColor = accent.copy(alpha = 0.3f)
            checkColor = onAccent.copy(alpha = 0.4f)
        }
        isChecked -> {
            boxColor = accent
            borderColor = accent
            checkColor = onAccent
        }
        else -> {
            boxColor = Color.Transparent
            borderColor = outline
            checkColor = Color.Transparent
        }
    }

    Box(
        modifier = modifier
            .size(dims.clickTargetSize)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) { onCheckedChange(!isChecked) },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(dims.checkboxSize)
                .clip(RoundedCornerShape(dims.cornerRadius))
                .background(boxColor)
                .border(dims.strokeWidth, borderColor, RoundedCornerShape(dims.cornerRadius)),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(dims.checkmarkSize)) {
                if (animatedProgress > 0f) {
                    val w = this.size.width
                    val h = this.size.height
                    val path = Path().apply {
                        moveTo(w * 0.2f, h * 0.5f)
                        lineTo(w * 0.45f, h * 0.75f)
                        lineTo(w * 0.85f, h * 0.25f)
                    }
                    drawPath(
                        path = path,
                        color = checkColor,
                        style = Stroke(
                            width = dims.strokeWidth.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = floatArrayOf(path.length, path.length),
                                phase = path.length * (1f - animatedProgress),
                            ),
                        ),
                    )
                }
            }
        }
    }
}

// ──────────────── Internal ────────────────

private data class CheckboxDimensions(
    val checkboxSize: Dp,
    val clickTargetSize: Dp,
    val cornerRadius: Dp,
    val strokeWidth: Dp,
    val checkmarkSize: Dp,
)

private val Path.length: Float
    get() {
        val measure = PathMeasure()
        measure.setPath(this, false)
        return measure.length
    }