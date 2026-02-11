package com.application.echo.core.common.model

enum class AppDispatchers {
    IO,   // Dispatcher for IO-bound tasks
    Default, // Dispatcher for CPU-intensive tasks
    Main, // Dispatcher for UI-related tasks
}