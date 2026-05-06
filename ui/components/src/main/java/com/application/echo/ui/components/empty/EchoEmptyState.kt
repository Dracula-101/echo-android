package com.application.echo.ui.components.empty

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Centered placeholder for empty lists and "drop something here" zones.
 *
 * ```kotlin
 * EchoEmptyState(
 *     title = "No photos yet",
 *     description = "Drop photos here, or pick from gallery",
 * )
 *
 * EchoEmptyState(
 *     title = "Inbox zero",
 *     description = "You're all caught up.",
 *     icon = IconResource.Vector(Icons.Default.Inbox),
 *     bordered = false,
 * )
 * ```
 *
 * @param title Bold heading shown at the top of the message.
 * @param description Optional muted body copy below the title.
 * @param icon Optional icon shown above the title.
 * @param bordered When true (default) the state is wrapped in a dashed border —
 *   matches the "drop zone" treatment from the design library.
 * @param action Optional trailing slot — typically a button.
 */
@Composable
fun EchoEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: IconResource? = null,
    bordered: Boolean = true,
    action: (@Composable () -> Unit)? = null,
) {
    val muted = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.6f)
    val containerShape = EchoTheme.shapes.card

    val container = Modifier
        .fillMaxWidth()
        .clip(containerShape)
        .background(EchoTheme.colorScheme.surface.low)
        .let {
            if (bordered) {
                it.dashedRoundedBorder(
                    color = EchoTheme.colorScheme.outline.color.copy(alpha = 0.6f),
                    strokeWidth = 1.5.dp,
                    cornerRadius = 12.dp,
                )
            } else it
        }
        .padding(horizontal = EchoTheme.spacing.padding.large, vertical = EchoTheme.spacing.padding.extraLarge)

    Column(
        modifier = modifier.then(container),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.small),
    ) {
        if (icon != null) {
            icon.Paint(
                modifier = Modifier.size(EchoTheme.dimen.icon.large),
                color = muted,
            )
        }
        Text(
            text = title,
            style = EchoTheme.typography.titleSmall,
            color = EchoTheme.colorScheme.surface.onColor,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        if (description != null) {
            Text(
                text = description,
                style = EchoTheme.typography.bodySmall,
                color = muted,
                textAlign = TextAlign.Center,
            )
        }
        if (action != null) {
            action()
        }
    }
}

private fun Modifier.dashedRoundedBorder(
    color: androidx.compose.ui.graphics.Color,
    strokeWidth: Dp,
    cornerRadius: Dp,
): Modifier = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
    )
    val inset = strokeWidth.toPx() / 2
    drawRoundRect(
        color = color,
        topLeft = Offset(inset, inset),
        size = Size(size.width - inset * 2, size.height - inset * 2),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = stroke,
    )
}
