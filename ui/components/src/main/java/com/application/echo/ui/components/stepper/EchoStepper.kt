package com.application.echo.ui.components.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.design.theme.EchoTheme

private val StepperHeight: Dp = 36.dp
private val StepperButtonSize: Dp = 36.dp
private val StepperValueWidth: Dp = 40.dp

/**
 * Numeric stepper with `−` and `+` controls and a centered value.
 *
 * ```kotlin
 * var qty by remember { mutableIntStateOf(1) }
 * EchoStepper(
 *     value = { qty },
 *     onValueChange = { qty = it },
 *     range = 0..10,
 * )
 * ```
 *
 * @param value Lambda returning the current value (deferred read).
 * @param onValueChange Called when the user taps − or +. Already bounded by [range].
 * @param range Allowed inclusive range. Default `0..Int.MAX_VALUE`.
 * @param step Increment / decrement amount. Default `1`.
 * @param enabled When false, both buttons are dimmed and ignore input.
 * @param variant Color family used for the active button states.
 */
@Composable
fun EchoStepper(
    value: () -> Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 0..Int.MAX_VALUE,
    step: Int = 1,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
) {
    val current = value().coerceIn(range)
    val canDecrease = enabled && current - step >= range.first
    val canIncrease = enabled && current + step <= range.last

    Row(
        modifier = modifier
            .clip(EchoTheme.shapes.button)
            .background(EchoTheme.colorScheme.surface.high),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton(
            symbol = "−",
            enabled = canDecrease,
            variant = variant,
            onClick = { onValueChange((current - step).coerceIn(range)) },
        )
        Box(
            modifier = Modifier
                .width(StepperValueWidth)
                .padding(horizontal = EchoTheme.spacing.padding.extraSmall),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = current.toString(),
                style = EchoTheme.typography.titleMedium,
                color = EchoTheme.colorScheme.surface.onColor,
                textAlign = TextAlign.Center,
            )
        }
        StepperButton(
            symbol = "+",
            enabled = canIncrease,
            variant = variant,
            onClick = { onValueChange((current + step).coerceIn(range)) },
        )
    }
}

@Composable
private fun StepperButton(
    symbol: String,
    enabled: Boolean,
    variant: EchoVariant,
    onClick: () -> Unit,
) {
    val accent = variant.color()
    val onSurface = EchoTheme.colorScheme.surface.onColor
    val color = if (enabled) onSurface else onSurface.copy(alpha = 0.3f)

    Box(
        modifier = Modifier
            .size(StepperButtonSize)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            style = EchoTheme.typography.titleLarge,
            color = if (enabled) accent else color,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
