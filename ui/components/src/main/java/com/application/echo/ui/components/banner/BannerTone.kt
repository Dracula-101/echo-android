package com.application.echo.ui.components.banner

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Semantic tone for [EchoBanner]. Maps to a color pair, an icon, and a tinted container.
 */
enum class BannerTone {
    /** Mint — confirmation, encryption, success states. */
    Success,

    /** Coral — informational primary callouts. */
    Info,

    /** Gold — non-blocking warnings. */
    Warning,

    /** Error red — blocking problems. */
    Error,
}

@Immutable
internal data class BannerColors(
    val container: Color,
    val content: Color,
    val accent: Color,
    val muted: Color,
)

@Composable
internal fun BannerTone.colors(): BannerColors {
    val scheme = EchoTheme.colorScheme
    val accent = when (this) {
        BannerTone.Success -> scheme.secondary.color
        BannerTone.Info -> scheme.primary.color
        BannerTone.Warning -> scheme.primary.dim
        BannerTone.Error -> scheme.error.color
    }
    val container = when (this) {
        BannerTone.Success -> scheme.secondary.container
        BannerTone.Info -> scheme.primary.container
        BannerTone.Warning -> scheme.primary.container
        BannerTone.Error -> scheme.error.container
    }
    val onContainer = when (this) {
        BannerTone.Success -> scheme.secondary.onContainer
        BannerTone.Info -> scheme.primary.onContainer
        BannerTone.Warning -> scheme.primary.onContainer
        BannerTone.Error -> scheme.error.onContainer
    }
    return BannerColors(
        container = container,
        content = onContainer,
        accent = accent,
        muted = onContainer.copy(alpha = 0.75f),
    )
}

internal fun BannerTone.icon(): ImageVector = when (this) {
    BannerTone.Success -> Icons.Filled.CheckCircle
    BannerTone.Info -> Icons.Filled.Info
    BannerTone.Warning -> Icons.Filled.Warning
    BannerTone.Error -> Icons.Filled.Error
}
