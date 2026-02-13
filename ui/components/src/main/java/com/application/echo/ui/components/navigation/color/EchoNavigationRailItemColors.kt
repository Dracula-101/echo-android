package com.application.echo.ui.components.navigation.color

import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Provides a default set of Echo-styled colors for navigation rail items.
 */
@Composable
fun echoNavigationRailItemColors(): NavigationRailItemColors = NavigationRailItemColors(
    selectedIconColor = EchoTheme.colorScheme.secondary.container,
    unselectedIconColor = EchoTheme.colorScheme.primary.color,
    disabledIconColor = EchoTheme.colorScheme.primary.dim,
    selectedTextColor = EchoTheme.colorScheme.secondary.container,
    unselectedTextColor = EchoTheme.colorScheme.primary.color,
    disabledTextColor = EchoTheme.colorScheme.primary.dim,
    selectedIndicatorColor = Color.Transparent,
)
