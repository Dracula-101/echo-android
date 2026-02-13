package com.application.echo.core.common.model

/**
 * Physical / logical screen properties.
 *
 * @param widthPx      Physical width in pixels
 * @param heightPx     Physical height in pixels
 * @param widthDp      Width in density-independent pixels
 * @param heightDp     Height in density-independent pixels
 * @param densityDpi   Exact DPI value reported by the display
 * @param densityBucket Closest density bucket
 * @param scaledDensity Font scale factor
 * @param refreshRate  Current display refresh rate in Hz
 * @param hdrCapable   Whether the display supports HDR
 * @param roundScreen  Whether the screen is circular (e.g. Wear OS)
 */
data class ScreenInfo(
    val widthPx: Int,
    val heightPx: Int,
    val widthDp: Float,
    val heightDp: Float,
    val densityDpi: Int,
    val densityBucket: ScreenDensityBucket,
    val scaledDensity: Float,
    val refreshRate: Float,
    val hdrCapable: Boolean,
    val roundScreen: Boolean
)
