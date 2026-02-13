package com.application.echo.core.common.model

/**
 * Application metadata.
 *
 * @param packageName       e.g. "com.example.myapp"
 * @param versionName       e.g. "2.4.1"
 * @param versionCode       Long version code
 * @param installerPackage  Package that installed this app (Play Store, sideload, etc.)
 * @param debuggable        Whether the app is built with debuggable=true
 * @param targetSdkVersion  Target SDK declared in the manifest
 * @param minSdkVersion     Min SDK declared in the manifest
 * @param firstInstallTime  Epoch millis of first install
 * @param lastUpdateTime    Epoch millis of last update
 */
data class AppInfo(
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val installerPackage: String?,
    val debuggable: Boolean,
    val targetSdkVersion: Int?,
    val minSdkVersion: Int,
    val firstInstallTime: Long,
    val lastUpdateTime: Long
)
