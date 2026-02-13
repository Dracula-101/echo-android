package com.application.echo.core.common.model

/**
 * Screen density bucket following Android DisplayMetrics conventions.
 */
enum class ScreenDensityBucket(val dpi: Int) {
    LDPI(120),
    MDPI(160),
    HDPI(240),
    XHDPI(320),
    XXHDPI(480),
    XXXHDPI(640),
    TVDPI(213),
    UNKNOWN(0)
}