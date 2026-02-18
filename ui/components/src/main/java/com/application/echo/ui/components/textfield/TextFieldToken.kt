package com.application.echo.ui.components.textfield

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.design.colors.EchoColorScheme

/**
 * Color palette for [EchoTextField].
 */
internal data class TextFieldColors(
    val text: Color,
    val placeholder: Color,
    val label: Color,
    val cursor: Color,
    val focusedBorder: Color,
    val unfocusedBorder: Color,
    val errorBorder: Color,
    val background: Color,
    val disabledText: Color,
    val disabledBorder: Color,
    val disabledBackground: Color,
    val helperText: Color,
    val errorText: Color,
)

@Composable
internal fun EchoColorScheme.textFieldColors(): TextFieldColors {
    return TextFieldColors(
        text = surface.onColor,
        placeholder = surface.onColor.copy(alpha = 0.5f),
        label = surface.onColor.copy(alpha = 0.7f),
        cursor = primary.color,
        focusedBorder = primary.color,
        unfocusedBorder = outline.color,
        errorBorder = error.color,
        background = surface.highest,
        disabledText = surface.onColor.copy(alpha = 0.4f),
        disabledBorder = outline.color.copy(alpha = 0.3f),
        disabledBackground = surface.color.copy(alpha = 0.5f),
        helperText = surface.onColor.copy(alpha = 0.6f),
        errorText = error.color,
    )
}
