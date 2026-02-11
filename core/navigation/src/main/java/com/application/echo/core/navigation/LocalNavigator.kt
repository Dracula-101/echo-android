package com.application.echo.core.navigation

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] providing the [Navigator]
 * instance throughout the Compose tree.
 *
 * Provided at the top level via [EchoNavHost] or a [CompositionLocalProvider][androidx.compose.runtime.CompositionLocalProvider].
 * Accessing this before it is provided will throw an error to surface misconfiguration early.
 */
val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided. Wrap your content with EchoNavHost or provide a Navigator via CompositionLocalProvider.")
}
