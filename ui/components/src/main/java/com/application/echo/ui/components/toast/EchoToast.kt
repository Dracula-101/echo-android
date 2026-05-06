package com.application.echo.ui.components.toast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Compact pill-shaped toast for transient progress / confirmation messages.
 *
 * Unlike [com.application.echo.ui.components.snackbar.EchoSnackbar], a toast is
 * purely visual — show / hide is the caller's responsibility (typically via an
 * `AnimatedVisibility` driven by a flow).
 *
 * ```kotlin
 * EchoToast(
 *     text = "2 messages saved",
 *     leading = ToastLeading.Success,
 * )
 * EchoToast(
 *     text = "Sending photo",
 *     leading = ToastLeading.Loading,
 * )
 * ```
 */
@Composable
fun EchoToast(
    text: String,
    modifier: Modifier = Modifier,
    leading: ToastLeading = ToastLeading.None,
    accentVariant: EchoVariant = EchoVariant.Primary,
) {
    val shape = EchoTheme.shapes.snackbar
    val container = EchoTheme.colorScheme.surface.high
    val content = EchoTheme.colorScheme.surface.onColor
    val accent = when (leading) {
        ToastLeading.Success -> EchoTheme.colorScheme.secondary.color
        ToastLeading.Error -> EchoTheme.colorScheme.error.color
        ToastLeading.Loading, ToastLeading.None -> accentVariant.color()
        is ToastLeading.Custom -> accentVariant.color()
    }
    val borderColor = when (leading) {
        ToastLeading.Success -> EchoTheme.colorScheme.secondary.color
        ToastLeading.Error -> EchoTheme.colorScheme.error.color
        else -> EchoTheme.colorScheme.outline.color.copy(alpha = 0.4f)
    }

    Row(
        modifier = modifier
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 18.dp,
                    spread = 0.dp,
                    color = Color.Black.copy(alpha = 0.18f),
                    offset = DpOffset(0.dp, 6.dp),
                ),
            )
            .clip(shape)
            .background(container)
            .border(EchoTheme.dimen.border.small, borderColor, shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.small),
    ) {
        when (leading) {
            ToastLeading.None -> Unit
            ToastLeading.Loading -> EchoToastSpinner(
                color = accent,
                modifier = Modifier.size(14.dp),
            )
            ToastLeading.Success, ToastLeading.Error -> {
                IconResource.Vector(
                    if (leading == ToastLeading.Success) Icons.Filled.CheckCircle else Icons.Filled.Error,
                ).Paint(
                    modifier = Modifier.size(14.dp),
                    color = accent,
                )
            }
            is ToastLeading.Custom -> leading.icon.Paint(
                modifier = Modifier.size(14.dp),
                color = accent,
            )
        }
        Text(
            text = text,
            style = EchoTheme.typography.bodySmall,
            color = content,
        )
    }
}
