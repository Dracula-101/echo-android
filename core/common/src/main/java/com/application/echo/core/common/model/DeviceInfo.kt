package com.application.echo.core.common.model

/**
 * Aggregate snapshot of all device information.
 * Immutable — create a new instance whenever a fresh snapshot is needed.
 */
data class DeviceInfo(
    // Identity
    val deviceId: String,

    // OS
    val osVersion: String,
    val osApiLevel: Int,
    val osCodename: String,
    val securityPatchLevel: String,
    val isPreviewBuild: Boolean,

    // Hardware / Build
    val manufacturer: String,
    val deviceName: String,
    val model: String,
    val board: String,
    val brand: String,
    val hardware: String,
    val buildFingerprint: String,
    val buildType: String,
    val buildId: String,
    val isRooted: Boolean,
    val isEmulator: Boolean,

    // Form factor
    val deviceType: DeviceType,

    // Screen
    val screenInfo: ScreenInfo,

    // Hardware
    val hardwareInfo: HardwareInfo,

    // Network
    val networkInfo: NetworkInfo,

    // App
    val appInfo: AppInfo,

    // Meta
    val snapshotTimestamp: Long = System.currentTimeMillis()
)