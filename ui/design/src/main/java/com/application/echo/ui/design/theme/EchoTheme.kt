package com.application.echo.ui.design.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.application.echo.ui.design.colors.EchoColorScheme
import com.application.echo.ui.design.colors.darkTheme
import com.application.echo.ui.design.colors.lightTheme
import com.application.echo.ui.design.colors.toMaterialColorScheme
import com.application.echo.ui.design.dimension.EchoDimensions
import com.application.echo.ui.design.dimension.echoDimensions
import com.application.echo.ui.design.shape.EchoShapes
import com.application.echo.ui.design.shape.echoShapes
import com.application.echo.ui.design.spacing.EchoSpacing
import com.application.echo.ui.design.spacing.echoSpacing
import com.application.echo.ui.design.type.EchoTypography
import com.application.echo.ui.design.type.echoTypography
import com.application.echo.ui.design.type.toMaterialTypography

object EchoTheme {
    /**
     * Retrieves the current [EchoColorScheme].
     */
    val colorScheme: EchoColorScheme

        @Composable
        @ReadOnlyComposable
        get() = LocalEchoColorScheme.current

    /**
     * Retrieves the current [EchoShapes].
     */
    val shapes: EchoShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalEchoShapes.current

    /**
     * Retrieves the current [EchoTypography].
     */
    val typography: EchoTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalEchoTypography.current

    /**
     * Retrieves the current [EchoSpacing].
     */
    val spacing: EchoSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalEchoSpacing.current

    /**
     * Retrieves the current [EchoDimensions].
     */
    val dimen: EchoDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalEchoDimensions.current
}

@Composable
fun EchoTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val echoColorScheme = when {
        isDarkTheme -> darkTheme()
        else -> lightTheme()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDarkTheme
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme
        }
    }

    CompositionLocalProvider(
        LocalEchoColorScheme provides echoColorScheme,
        LocalEchoShapes provides echoShapes,
        LocalEchoTypography provides echoTypography,
        LocalEchoSpacing provides echoSpacing,
        LocalEchoDimensions provides echoDimensions
    ) {
        MaterialTheme(
            colorScheme = echoColorScheme.toMaterialColorScheme(),
            typography = echoTypography.toMaterialTypography(),
            content = content
        )
    }
}


val LocalEchoColorScheme: ProvidableCompositionLocal<EchoColorScheme> =
    compositionLocalOf { darkTheme() }

val LocalEchoShapes: ProvidableCompositionLocal<EchoShapes> =
    compositionLocalOf { echoShapes }

val LocalEchoTypography: ProvidableCompositionLocal<EchoTypography> =
    compositionLocalOf { echoTypography }

val LocalEchoSpacing: ProvidableCompositionLocal<EchoSpacing> =
    compositionLocalOf { echoSpacing }

val LocalEchoDimensions: ProvidableCompositionLocal<EchoDimensions> =
    compositionLocalOf { echoDimensions }