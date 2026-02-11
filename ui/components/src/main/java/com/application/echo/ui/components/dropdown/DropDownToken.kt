package com.application.echo.ui.components.dropdown

import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.design.colors.EchoColorScheme

data class DropdownColors(
    val background: Color,
    val border: Color,
    val scrollTrack: Color,
    val scrollThumb: Color
)

@Composable
internal fun EchoColorScheme.dropdownColors(): DropdownColors {
    return DropdownColors(
        background = surface.container,         // Card-like container for dropdown
        border = surface.high,                 // Default light gray border
        scrollTrack = surface.lowest,           // Subtle background track
        scrollThumb = outline.variant           // Slightly darker for visibility
    )
}

@Composable
internal fun EchoColorScheme.dropdownCardColors(): CardColors {
    return CardDefaults.cardColors()
}