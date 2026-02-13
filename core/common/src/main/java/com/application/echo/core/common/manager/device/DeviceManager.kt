package com.application.echo.core.common.manager.device

import com.application.echo.core.common.model.AppInfo
import com.application.echo.core.common.model.DeviceInfo
import com.application.echo.core.common.model.DeviceType
import com.application.echo.core.common.model.HardwareInfo
import com.application.echo.core.common.model.NetworkInfo
import com.application.echo.core.common.model.ScreenInfo

/**
 * DeviceManager
 *
 * A contract for retrieving comprehensive device information on Android.
 * All properties are read-only and resolved lazily or eagerly by the implementation.
 *
 * Usage:
 *   val deviceManager: DeviceManager = DeviceManagerImpl(context)
 *   val info: DeviceInfo = deviceManager.getDeviceInfo()
 */
interface DeviceManager {

    // ─────────────────────────────────────────────
    //  Identity
    // ─────────────────────────────────────────────

    /**
     * Returns a unique device identifier.
     *
     * Strategy (in priority order):
     *  1. ANDROID_ID (stable across factory resets only in Android 8+)
     *  2. MediaDrm Widevine UUID (hardware-bound, survives factory resets on supported devices)
     *  3. Generated UUID persisted in EncryptedSharedPreferences (fallback)
     *
     * NOTE: READ_PHONE_STATE permission is NOT required for this implementation.
     * The function is @throws-free — it never propagates exceptions to callers.
     */
    fun getDeviceId(): String

    // ─────────────────────────────────────────────
    //  OS / Platform
    // ─────────────────────────────────────────────

    /** Android OS version string, e.g. "14" */
    fun getOsVersion(): String

    /** Android API level, e.g. 34 */
    fun getOsApiLevel(): Int

    /** Android release codename if available, e.g. "UpsideDownCake" */
    fun getOsCodename(): String

    /** Security patch level, e.g. "2024-03-05" */
    fun getSecurityPatchLevel(): String

    /** Whether the device is running a pre-release / preview build */
    fun isPreviewBuild(): Boolean

    // ─────────────────────────────────────────────
    //  Hardware / Build
    // ─────────────────────────────────────────────

    /** Device manufacturer, e.g. "Samsung", "Google" */
    fun getManufacturer(): String

    /** Marketing / consumer name, e.g. "Pixel 8 Pro" */
    fun getDeviceName(): String

    /** Hardware model string, e.g. "SM-S918B" */
    fun getModel(): String

    /** Build board, e.g. "kalama" */
    fun getBoard(): String

    /** Build brand, e.g. "google" */
    fun getBrand(): String

    /** Hardware name, e.g. "qcom" */
    fun getHardware(): String

    /** Build fingerprint (unique per build) */
    fun getBuildFingerprint(): String

    /** Build type, e.g. "user", "userdebug", "eng" */
    fun getBuildType(): String

    /** Build ID / incremental, e.g. "UP1A.231105.001" */
    fun getBuildId(): String

    /** Whether the device is rooted (best-effort heuristic) */
    fun isRooted(): Boolean

    /** Whether the device is an emulator */
    fun isEmulator(): Boolean

    // ─────────────────────────────────────────────
    //  Form Factor / Type
    // ─────────────────────────────────────────────

    /**
     * High-level device type classification.
     * @see DeviceType
     */
    fun getDeviceType(): DeviceType

    /** Whether the device supports phone calls */
    fun isPhone(): Boolean

    /** Whether the device is a tablet (sw >= 600dp) */
    fun isTablet(): Boolean

    /** Whether the device is foldable */
    fun isFoldable(): Boolean

    /** Whether the device is running Android TV / Google TV */
    fun isTV(): Boolean

    /** Whether the device is a Wear OS watch */
    fun isWatch(): Boolean

    /** Whether the device is an Android Auto device */
    fun isAutomotive(): Boolean

    // ─────────────────────────────────────────────
    //  Screen
    // ─────────────────────────────────────────────

    /**
     * Screen metrics snapshot.
     * @see ScreenInfo
     */
    fun getScreenInfo(): ScreenInfo

    // ─────────────────────────────────────────────
    //  CPU / Memory
    // ─────────────────────────────────────────────

    /**
     * CPU and memory metrics.
     * @see HardwareInfo
     */
    fun getHardwareInfo(): HardwareInfo

    // ─────────────────────────────────────────────
    //  Network
    // ─────────────────────────────────────────────

    /**
     * Network connectivity snapshot.
     * Requires ACCESS_NETWORK_STATE permission.
     * @see NetworkInfo
     */
    fun getNetworkInfo(): NetworkInfo

    // ─────────────────────────────────────────────
    //  App / Build Metadata
    // ─────────────────────────────────────────────

    /**
     * App-level metadata (version, package, installer).
     * @see AppInfo
     */
    fun getAppInfo(): AppInfo

    // ─────────────────────────────────────────────
    //  Aggregate
    // ─────────────────────────────────────────────

    /**
     * Returns all device information bundled into a single [DeviceInfo] snapshot.
     * Advertising ID is excluded; call [getAdvertisingId] separately if needed.
     */
    fun getDeviceInfo(): DeviceInfo

    /**
     * Serialises [DeviceInfo] to a flat Map<String, String> suitable for
     * analytics / logging / remote config payloads.
     */
    fun getDeviceInfoAsMap(): Map<String, String>
}