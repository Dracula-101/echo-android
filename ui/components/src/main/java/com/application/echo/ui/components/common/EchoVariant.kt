package com.application.echo.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Shared color variant used across all Echo components.
 *
 * Every component that supports theming (buttons, badges, progress,
 * checkboxes, switches, etc.) uses this enum to select its color palette.
 *
 * ```kotlin
 * EchoFilledButton(text = "Delete", variant = EchoVariant.Error, onClick = { … })
 * EchoBadge(text = "New", variant = EchoVariant.Secondary)
 * ```
 */
enum class EchoVariant {

    /** Coral red — primary calls to action. */
    Primary,

    /** Teal — secondary / alternative emphasis. */
    Secondary,

    /** Red — destructive or error actions. */
    Error,
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Color resolver
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Resolves the main [Color] for a given [EchoVariant] from the current theme.
 */
@Composable
fun EchoVariant.color(): Color = when (this) {
    EchoVariant.Primary -> EchoTheme.colorScheme.primary.color
    EchoVariant.Secondary -> EchoTheme.colorScheme.secondary.color
    EchoVariant.Error -> EchoTheme.colorScheme.error.color
}

/**
 * Resolves the "on" [Color] (content on top of [color]) for a given [EchoVariant].
 */
@Composable
fun EchoVariant.onColor(): Color = when (this) {
    EchoVariant.Primary -> EchoTheme.colorScheme.primary.onColor
    EchoVariant.Secondary -> EchoTheme.colorScheme.secondary.onColor
    EchoVariant.Error -> EchoTheme.colorScheme.error.onColor
}

/**
 * Resolves the container [Color] for a given [EchoVariant].
 */
@Composable
fun EchoVariant.containerColor(): Color = when (this) {
    EchoVariant.Primary -> EchoTheme.colorScheme.primary.container
    EchoVariant.Secondary -> EchoTheme.colorScheme.secondary.container
    EchoVariant.Error -> EchoTheme.colorScheme.error.container
}

/**
 * Resolves the "on container" [Color] for a given [EchoVariant].
 */
@Composable
fun EchoVariant.onContainerColor(): Color = when (this) {
    EchoVariant.Primary -> EchoTheme.colorScheme.primary.onContainer
    EchoVariant.Secondary -> EchoTheme.colorScheme.secondary.onContainer
    EchoVariant.Error -> EchoTheme.colorScheme.error.onContainer
}
