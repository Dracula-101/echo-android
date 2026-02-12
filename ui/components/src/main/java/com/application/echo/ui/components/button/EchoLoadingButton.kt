package com.application.echo.ui.components.button

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Outlined button that shows a spinner when [loading] is `true`.
 *
 * The button is automatically disabled while loading.
 *
 * ```kotlin
 * EchoLoadingButton(
 *     text = "Sign In",
 *     loading = isSubmitting,
 *     onClick = ::login,
 * )
 * ```
 */
@Composable
fun EchoLoadingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
) {
    EchoLoadingButton(
        onClick = onClick,
        modifier = modifier,
        loading = loading,
        enabled = enabled,
        variant = variant,
    ) {
        Text(text = text)
    }
}

/**
 * Outlined button with spinner and composable content slot.
 *
 * Use the [text] overload when you only need a label.
 */
@Composable
fun EchoLoadingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    content: @Composable () -> Unit,
) {
    EchoOutlinedButton(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier,
        variant = variant,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(EchoTheme.dimen.icon.small),
                strokeWidth = 2.dp,
                color = variant.color().copy(alpha = 0.6f),
            )
            Spacer(Modifier.width(EchoTheme.spacing.gap.small))
        }
        content()
    }
}