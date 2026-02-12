package com.application.echo.ui.components.dropdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.design.colors.EchoColorScheme

/**
 * Color palette for the [EchoDropdownMenu] family.
 */
internal data class DropdownColors(
    val background: Color,
    val border: Color,
    val scrollTrack: Color,
    val scrollThumb: Color,
)

@Composable
internal fun EchoColorScheme.dropdownColors(): DropdownColors {
    return DropdownColors(
        background = surface.container,
        border = surface.high,
        scrollTrack = surface.lowest,
        scrollThumb = outline.variant,
    )
}
