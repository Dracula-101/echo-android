package com.application.echo.ui.components.avatar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Presence / status indicator rendered as a colored dot at the bottom-right
 * of an [EchoAvatar].
 */
enum class AvatarStatus {
    /** No status dot is shown. */
    None,

    /** Mint green — user is online and reachable. */
    Online,

    /** Gold — user is idle / away. */
    Idle,

    /** Surface gray — user is offline. */
    Offline,
}

@Composable
internal fun AvatarStatus.color(): Color = when (this) {
    AvatarStatus.None -> Color.Transparent
    AvatarStatus.Online -> OnlineColor
    AvatarStatus.Idle -> EchoTheme.colorScheme.secondary.color
    AvatarStatus.Offline -> EchoTheme.colorScheme.outline.color
}

private val OnlineColor = Color(0xFF22C55E)
