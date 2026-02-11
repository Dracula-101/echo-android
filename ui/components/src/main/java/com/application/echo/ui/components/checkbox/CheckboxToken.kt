package com.application.echo.ui.components.checkbox

import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import com.application.echo.ui.design.colors.EchoColorScheme

@Composable
fun EchoColorScheme.checkboxColors(): CheckboxColors {
    return CheckboxDefaults.colors(
        checkedColor = primary.color,
        uncheckedColor = primary.color,
        disabledCheckedColor = primary.color.copy(alpha = 0.4f),
        disabledUncheckedColor = primary.color.copy(alpha = 0.4f),
        checkmarkColor = primary.onColor,
        disabledIndeterminateColor = primary.color.copy(alpha = 0.2f),
    )
}