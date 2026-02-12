package com.application.echo.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Outlined button — secondary call-to-action style with a border stroke.
 *
 * ```kotlin
 * EchoOutlinedButton(text = "Cancel", onClick = ::dismiss)
 * EchoOutlinedButton(text = "Like", icon = IconResource.Vector(Icons.Default.Favorite), onClick = ::like)
 * ```
 */
@Composable
fun EchoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    icon: IconResource? = null,
) {
    EchoOutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant,
        icon = icon,
    ) {
        Text(text = text)
    }
}

/**
 * Outlined button with composable content slot.
 *
 * Use the [text] overload when you only need a label.
 */
@Composable
fun EchoOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    icon: IconResource? = null,
    content: @Composable () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = EchoTheme.colorScheme.outlinedButtonColors(variant),
        shape = EchoTheme.shapes.button,
        border = BorderStroke(
            width = EchoTheme.dimen.border.medium,
            color = EchoTheme.colorScheme.outlinedButtonBorderColor(variant, enabled),
        ),
        contentPadding = PaddingValues(
            horizontal = EchoTheme.spacing.padding.medium,
            vertical = EchoTheme.spacing.padding.small,
        ),
        content = {
            if (icon != null) {
                icon.Paint(
                    modifier = Modifier.size(EchoTheme.dimen.icon.small),
                )
                Spacer(Modifier.width(EchoTheme.spacing.gap.extraSmall))
            }
            ProvideTextStyle(
                value = EchoTheme.typography.bodyLarge,
                content = content,
            )
        },
    )
}