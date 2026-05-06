package com.application.echo.ui.components.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Discrete button sizes.
 *
 * Defines padding, label typography, and inline-icon size for buttons. Use
 * [Medium] (the default) for primary actions; [Small] for in-row CTAs and
 * dense surfaces; [Large] for hero / sign-up surfaces.
 */
enum class ButtonSize {
    Small,
    Medium,
    Large,
}

@Composable
@ReadOnlyComposable
internal fun ButtonSize.contentPadding(): PaddingValues = when (this) {
    ButtonSize.Small -> PaddingValues(horizontal = 14.dp, vertical = 6.dp)
    ButtonSize.Medium -> PaddingValues(horizontal = 18.dp, vertical = 10.dp)
    ButtonSize.Large -> PaddingValues(horizontal = 24.dp, vertical = 14.dp)
}

@Composable
@ReadOnlyComposable
internal fun ButtonSize.textStyle(): TextStyle = when (this) {
    ButtonSize.Small -> EchoTheme.typography.labelMedium
    ButtonSize.Medium -> EchoTheme.typography.bodyMedium
    ButtonSize.Large -> EchoTheme.typography.titleMedium
}

internal val ButtonSize.iconSize: Dp
    get() = when (this) {
        ButtonSize.Small -> 14.dp
        ButtonSize.Medium -> 16.dp
        ButtonSize.Large -> 18.dp
    }

internal val ButtonSize.iconGap: Dp
    get() = when (this) {
        ButtonSize.Small -> 6.dp
        ButtonSize.Medium -> 8.dp
        ButtonSize.Large -> 10.dp
    }

internal val ButtonSize.borderWidth: Dp
    get() = when (this) {
        ButtonSize.Small -> 1.dp
        ButtonSize.Medium -> 1.5.dp
        ButtonSize.Large -> 2.dp
    }