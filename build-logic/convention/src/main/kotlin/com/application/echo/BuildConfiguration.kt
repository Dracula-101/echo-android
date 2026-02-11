package com.application.echo

internal enum class BuildType { DEBUG, RELEASE }
internal enum class Flavor { DEVELOPMENT, PRODUCTION }

internal fun BuildType.applicationIdSuffix(): String? = when (this) {
    BuildType.DEBUG -> ".debug"
    BuildType.RELEASE -> null
}

internal fun BuildType.versionNameSuffix(): String? = when (this) {
    BuildType.DEBUG -> "-debug"
    BuildType.RELEASE -> null
}

internal fun BuildType.isMinifyEnabled(): Boolean = when (this) {
    BuildType.DEBUG -> false
    BuildType.RELEASE -> true
}

internal fun BuildType.isShrinkResourcesEnabled(): Boolean = when (this) {
    BuildType.DEBUG -> false
    BuildType.RELEASE -> true
}

internal fun BuildType.isCrashlyticsEnabled(): Boolean = when (this) {
    BuildType.DEBUG -> false
    BuildType.RELEASE -> true
}

internal fun BuildType.isAnalyticsEnabled(): Boolean = when (this) {
    BuildType.DEBUG -> false
    BuildType.RELEASE -> true
}

internal fun BuildType.isPerformanceMonitoringEnabled(): Boolean = when (this) {
    BuildType.DEBUG -> false
    BuildType.RELEASE -> true
}

internal fun BuildType.isFirebaseEnabled(): Boolean = when (this) {
    BuildType.DEBUG -> false
    BuildType.RELEASE -> true
}

internal fun Flavor.applicationIdSuffix(): String? = when (this) {
    Flavor.DEVELOPMENT -> ".dev"
    Flavor.PRODUCTION -> null
}

internal fun Flavor.versionNameSuffix(): String? = when (this) {
    Flavor.DEVELOPMENT -> "-dev"
    Flavor.PRODUCTION -> null
}

internal fun Flavor.apiBaseUrl(): String = when (this) {
    Flavor.DEVELOPMENT -> "https://api-dev.application.echo.com/"
    Flavor.PRODUCTION -> "https://api.application.echo.com/"
}

internal fun Flavor.webBaseUrl(): String = when (this) {
    Flavor.DEVELOPMENT -> "https://dev.application.echo.com/"
    Flavor.PRODUCTION -> "https://application.echo.com/"
}

internal fun Flavor.isLoggingEnabled(): Boolean = when (this) {
    Flavor.DEVELOPMENT -> true
    Flavor.PRODUCTION -> false
}

internal fun Flavor.isStrictModeEnabled(): Boolean = when (this) {
    Flavor.DEVELOPMENT -> true
    Flavor.PRODUCTION -> false
}

internal fun Flavor.appName(): String = when (this) {
    Flavor.DEVELOPMENT -> "Echo Dev"
    Flavor.PRODUCTION -> "Echo"
}

internal fun Flavor.appIcon(): String = "@mipmap/ic_launcher"

internal fun Flavor.isFirebaseEnabled(): Boolean = when (this) {
    Flavor.DEVELOPMENT -> false
    Flavor.PRODUCTION -> true
}
