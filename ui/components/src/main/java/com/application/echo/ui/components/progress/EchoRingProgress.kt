package com.application.echo.ui.components.progress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Determinate circular progress with a centered percentage label.
 *
 * Wraps [EchoCircularProgressIndicator] and adds the inline label seen in the
 * "Storage" / "Profile completeness" surfaces of the mockup.
 *
 * ```kotlin
 * EchoRingProgress(progress = { 0.63f })
 * EchoRingProgress(progress = { 0.63f }, label = { "${(it * 100).toInt()}%" })
 * ```
 *
 * @param progress Lambda returning the current value in `[0f..1f]` (deferred).
 * @param size Outer diameter. Default `64.dp`.
 * @param strokeWidth Ring stroke. Default `5.dp`.
 * @param label Renders the centered text. Receives the resolved progress.
 *   Pass `null` to hide.
 */
@Composable
fun EchoRingProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    variant: EchoVariant = EchoVariant.Primary,
    size: Dp = 64.dp,
    strokeWidth: Dp = 5.dp,
    label: ((Float) -> String)? = { "${(it * 100).toInt()}%" },
) {
    val resolved = progress().coerceIn(0f, 1f)
    val accent = variant.color()

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        EchoCircularProgressIndicator(
            progress = { resolved },
            variant = variant,
            color = accent,
            size = size,
            strokeWidth = strokeWidth,
        )
        if (label != null) {
            Text(
                text = label(resolved),
                style = EchoTheme.typography.labelSmall,
                color = EchoTheme.colorScheme.surface.onColor,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
