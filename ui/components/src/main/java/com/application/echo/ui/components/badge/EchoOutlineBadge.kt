package com.application.echo.ui.components.badge

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.util.IconResource

/**
 * Convenience wrapper — an [EchoBadge] with [EchoBadgeStyle.Outlined].
 *
 * Kept for backward compatibility. New code should use
 * `EchoBadge(style = EchoBadgeStyle.Outlined)`.
 */
@Composable
fun EchoOutlineBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: EchoVariant = EchoVariant.Primary,
    icon: IconResource? = null,
) {
    EchoBadge(
        text = text,
        modifier = modifier,
        variant = variant,
        style = EchoBadgeStyle.Outlined,
        icon = icon,
    )
}