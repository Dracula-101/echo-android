package com.application.echo.core.analytics

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAnalytics = staticCompositionLocalOf<Analytics> {
    NoOpAnalytics()
}
