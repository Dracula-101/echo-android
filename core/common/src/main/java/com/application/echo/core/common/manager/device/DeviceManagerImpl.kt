package com.application.echo.core.common.manager.device

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.media.MediaDrm
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.application.echo.core.common.model.DeviceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import androidx.core.content.edit
import com.application.echo.core.common.annotations.EncryptedPreferences
import com.application.echo.core.common.model.AppInfo
import com.application.echo.core.common.model.ConnectionType
import com.application.echo.core.common.model.DeviceInfo
import com.application.echo.core.common.model.HardwareInfo
import com.application.echo.core.common.model.NetworkInfo
import com.application.echo.core.common.model.ScreenDensityBucket
import com.application.echo.core.common.model.ScreenInfo

/**
 * DeviceManagerImpl
 *
 * Thread-safe, production-grade implementation of [DeviceManager].
 *
 * Dependencies (add to build.gradle.kts):
 * ─────────────────────────────────────────────────────────────────────────────
 *  implementation("androidx.security:security-crypto:1.1.0-alpha06")
 *  implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * Required permissions in AndroidManifest.xml:
 * ─────────────────────────────────────────────────────────────────────────────
 *  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *  <!-- Optional: READ_PHONE_STATE for SIM count on older APIs -->
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * Recommended usage (Hilt / Koin singleton):
 *   val deviceManager: DeviceManager = DeviceManagerImpl(applicationContext)
 */
class DeviceManagerImpl(
    private val context: Context,
    @EncryptedPreferences private val encryptedSharedPreferences: SharedPreferences,
) : DeviceManager {

    // ─────────────────────────────────────────────
    //  Constants
    // ─────────────────────────────────────────────

    companion object {
        private const val PREFS_NAME          = "device_manager_prefs"
        private const val KEY_DEVICE_ID       = "device_id"
        private val WIDEVINE_UUID             = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)

        // Known emulator fingerprint tokens
        private val EMULATOR_FINGERPRINTS = listOf(
            "generic", "unknown", "google_sdk", "emulator", "Android SDK built for"
        )

        // Strings found in rooted devices
        private val ROOT_INDICATORS = listOf(
            "/system/app/Superuser.apk",
            "/system/xbin/su",
            "/system/bin/su",
            "/sbin/su",
            "/system/su",
            "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su-backup",
            "/data/local/su",
            "/data/local/bin/su",
            "/data/local/xbin/su",
        )
    }

    // ─────────────────────────────────────────────
    //  Lazily-resolved system services
    // ─────────────────────────────────────────────

    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val telephonyManager by lazy {
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    private val activityManager by lazy {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }

    private val windowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val displayManager by lazy {
        context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    // ─────────────────────────────────────────────
    //  Identity
    // ─────────────────────────────────────────────

    @SuppressLint("HardwareIds")
    override fun getDeviceId(): String {
        return runCatching { getAndroidId() }
            .getOrNull()
            ?.takeIf { it.isNotBlank() && it != "9774d56d682e549c" } // known invalid ID
            ?: runCatching { getWidevineDrmId() }.getOrNull()
            ?: getOrCreatePersistentId()
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidId(): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    private fun getWidevineDrmId(): String? {
        return runCatching {
            val drm = MediaDrm(WIDEVINE_UUID)
            val id = drm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) drm.close() else drm.release()
            id.let { bytes ->
                bytes.joinToString("") { "%02x".format(it) }
            }
        }.getOrNull()
    }

    private fun getOrCreatePersistentId(): String {
        var id = encryptedSharedPreferences.getString(KEY_DEVICE_ID, null)
        if (id == null) {
            id = UUID.randomUUID().toString()
            encryptedSharedPreferences.edit { putString(KEY_DEVICE_ID, id) }
        }
        return id
    }

    // ─────────────────────────────────────────────
    //  OS / Platform
    // ─────────────────────────────────────────────

    override fun getOsVersion(): String = Build.VERSION.RELEASE

    override fun getOsApiLevel(): Int = Build.VERSION.SDK_INT

    override fun getOsCodename(): String = Build.VERSION.CODENAME

    override fun getSecurityPatchLevel(): String = Build.VERSION.SECURITY_PATCH

    override fun isPreviewBuild(): Boolean =
        Build.VERSION.CODENAME.isNotEmpty() && Build.VERSION.CODENAME != "REL"

    // ─────────────────────────────────────────────
    //  Hardware / Build
    // ─────────────────────────────────────────────

    override fun getManufacturer(): String = Build.MANUFACTURER.capitalize()

    override fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer, ignoreCase = true)) {
            model.capitalize()
        } else {
            "${manufacturer.capitalize()} $model"
        }
    }

    override fun getModel(): String = Build.MODEL

    override fun getBoard(): String = Build.BOARD

    override fun getBrand(): String = Build.BRAND

    override fun getHardware(): String = Build.HARDWARE

    override fun getBuildFingerprint(): String = Build.FINGERPRINT

    override fun getBuildType(): String = Build.TYPE

    override fun getBuildId(): String = Build.ID

    /**
     * Root detection using a multi-heuristic approach.
     * No heuristic is 100% reliable; this combines several signals.
     */
    override fun isRooted(): Boolean {
        // 1. Check for known root binary paths
        if (ROOT_INDICATORS.any { File(it).exists() }) return true

        // 2. Check for test-keys build tag (common in rooted / custom ROMs)
        if (Build.TAGS?.contains("test-keys") == true) return true

        // 3. Try to execute 'su' from PATH
        val suExitCode = runCatching {
            Runtime.getRuntime().exec(arrayOf("which", "su")).waitFor()
        }.getOrDefault(-1)
        if (suExitCode == 0) return true

        // 4. Check for Magisk manager packages
        val magiskPackages = listOf(
            "com.topjohnwu.magisk",
            "io.github.huskydg.magisk",
            "com.fox2code.mmm"
        )
        val pm = context.packageManager
        if (magiskPackages.any { packageExists(pm, it) }) return true

        return false
    }

    /**
     * Emulator detection using multiple fingerprints and system properties.
     */
    override fun isEmulator(): Boolean {
        val buildDetails = listOf(
            Build.FINGERPRINT, Build.HARDWARE, Build.MODEL,
            Build.PRODUCT, Build.BRAND, Build.DEVICE
        ).joinToString(" ").lowercase()

        if (EMULATOR_FINGERPRINTS.any { token -> buildDetails.contains(token.lowercase()) }) {
            return true
        }

        // Check classic emulator properties
        if (Build.HARDWARE == "goldfish" || Build.HARDWARE == "ranchu") return true
        if (Build.PRODUCT.contains("sdk") || Build.PRODUCT.contains("emulator")) return true

        // Check for emulator-specific files
        return File("/dev/socket/qemud").exists() ||
                File("/dev/qemu_pipe").exists() ||
                File("/.nativebridge").exists()
    }

    // ─────────────────────────────────────────────
    //  Form Factor / Type
    // ─────────────────────────────────────────────

    override fun getDeviceType(): DeviceType = when {
        isWatch()      -> DeviceType.WATCH
        isTV()         -> DeviceType.TV
        isAutomotive() -> DeviceType.AUTOMOTIVE
        isFoldable()   -> DeviceType.FOLDABLE
        isTablet()     -> DeviceType.TABLET
        isPhone()      -> DeviceType.PHONE
        else           -> DeviceType.UNKNOWN
    }

    override fun isPhone(): Boolean =
        telephonyManager.phoneType != TelephonyManager.PHONE_TYPE_NONE

    override fun isTablet(): Boolean {
        val metrics = context.resources.displayMetrics
        val swDp = minOf(
            metrics.widthPixels  / metrics.density,
            metrics.heightPixels / metrics.density
        )
        return swDp >= 600f
    }

    override fun isFoldable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return runCatching {
                // WindowManager Jetpack / Flexible display heuristic
                val displays = displayManager.displays
                displays.size > 1
            }.getOrDefault(false)
        }
        return false
    }

    override fun isTV(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEVISION) ||
                context.packageManager.hasSystemFeature("android.software.leanback")

    override fun isWatch(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)

    override fun isAutomotive(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)

    // ─────────────────────────────────────────────
    //  Screen
    // ─────────────────────────────────────────────

    override fun getScreenInfo(): ScreenInfo {
        val metrics = DisplayMetrics()

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(metrics)
        } else {
            windowManager.defaultDisplay.getRealMetrics(metrics)
        }

        val densityBucket = densityBucketFor(metrics.densityDpi)
        val refreshRate   = getRefreshRate()
        val hdrCapable    = isHdrCapable()
        val roundScreen   = context.resources.configuration.isScreenRound

        return ScreenInfo(
            widthPx         = metrics.widthPixels,
            heightPx        = metrics.heightPixels,
            widthDp         = metrics.widthPixels  / metrics.density,
            heightDp        = metrics.heightPixels / metrics.density,
            densityDpi      = metrics.densityDpi,
            densityBucket   = densityBucket,
            scaledDensity   = metrics.scaledDensity,
            refreshRate     = refreshRate,
            hdrCapable      = hdrCapable,
            roundScreen     = roundScreen
        )
    }

    private fun densityBucketFor(dpi: Int): ScreenDensityBucket = when {
        dpi <= 120 -> ScreenDensityBucket.LDPI
        dpi <= 160 -> ScreenDensityBucket.MDPI
        dpi <= 213 -> ScreenDensityBucket.TVDPI
        dpi <= 240 -> ScreenDensityBucket.HDPI
        dpi <= 320 -> ScreenDensityBucket.XHDPI
        dpi <= 480 -> ScreenDensityBucket.XXHDPI
        dpi <= 640 -> ScreenDensityBucket.XXXHDPI
        else       -> ScreenDensityBucket.UNKNOWN
    }

    private fun getRefreshRate(): Float = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.refreshRate ?: 60f
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.refreshRate
        }
    }.getOrDefault(60f)

    private fun isHdrCapable(): Boolean = runCatching {
        displayManager.displays.any { it.refreshRate > 60f }
    }.getOrDefault(false)
    // ─────────────────────────────────────────────
    //  CPU / Memory
    // ─────────────────────────────────────────────

    override fun getHardwareInfo(): HardwareInfo {
        val memInfo = ActivityManager.MemoryInfo().also { activityManager.getMemoryInfo(it) }

        return HardwareInfo(
            cpuAbi              = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown",
            supportedAbis       = Build.SUPPORTED_ABIS.toList(),
            cpuCores            = Runtime.getRuntime().availableProcessors(),
            cpuFrequencyKhz     = readCpuMaxFrequency(),
            totalRamBytes       = memInfo.totalMem,
            availableRamBytes   = memInfo.availMem,
            lowRamDevice        = activityManager.isLowRamDevice,
            totalStorageBytes   = getTotalInternalStorage(),
            availableStorageBytes = getAvailableInternalStorage()
        )
    }

    private fun readCpuMaxFrequency(): Long = runCatching {
        File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
            .readText().trim().toLong()
    }.getOrDefault(-1L)

    private fun getTotalInternalStorage(): Long = runCatching {
        StatFs(Environment.getDataDirectory().path).run {
            blockCountLong * blockSizeLong
        }
    }.getOrDefault(-1L)

    private fun getAvailableInternalStorage(): Long = runCatching {
        StatFs(Environment.getDataDirectory().path).run {
            availableBlocksLong * blockSizeLong
        }
    }.getOrDefault(-1L)

    // ─────────────────────────────────────────────
    //  Network
    // ─────────────────────────────────────────────

    @SuppressLint("MissingPermission")
    override fun getNetworkInfo(): NetworkInfo {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities  = connectivityManager.getNetworkCapabilities(activeNetwork)

        val isConnected = capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))

        val connectionType = resolveConnectionType(capabilities)
        val isMetered      = connectivityManager.isActiveNetworkMetered
        val isVpn          = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

        return NetworkInfo(
            isConnected      = isConnected,
            connectionType   = connectionType,
            isMetered        = isMetered,
            isVpnActive      = isVpn,
            networkOperator  = runCatching { telephonyManager.networkOperatorName }.getOrDefault(""),
            networkCountryIso = runCatching { telephonyManager.networkCountryIso }.getOrDefault(""),
            simCount         = getSimCount()
        )
    }

    private fun resolveConnectionType(capabilities: NetworkCapabilities?): ConnectionType {
        if (capabilities == null) return ConnectionType.NONE
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)     -> ConnectionType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> ConnectionType.BLUETOOTH
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> resolveCellularType()
            else -> ConnectionType.UNKNOWN
        }
    }

    @SuppressLint("MissingPermission")
    private fun resolveCellularType(): ConnectionType = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when (telephonyManager.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN     -> ConnectionType.CELLULAR_2G

                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP   -> ConnectionType.CELLULAR_3G

                TelephonyManager.NETWORK_TYPE_LTE     -> ConnectionType.CELLULAR_4G_LTE

                TelephonyManager.NETWORK_TYPE_NR      -> ConnectionType.CELLULAR_5G

                else                                  -> ConnectionType.UNKNOWN
            }
        } else ConnectionType.UNKNOWN
    }.getOrDefault(ConnectionType.UNKNOWN)

    @SuppressLint("MissingPermission")
    private fun getSimCount(): Int = runCatching {
        val sm = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE)
        if (sm is android.telephony.SubscriptionManager) {
            sm.activeSubscriptionInfoCount
        } else 1
    }.getOrDefault(1)

    // ─────────────────────────────────────────────
    //  App / Build Metadata
    // ─────────────────────────────────────────────

    @Suppress("DEPRECATION")
    override fun getAppInfo(): AppInfo {
        val pm      = context.packageManager
        val pkgName = context.packageName
        val pkgInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(pkgName, PackageManager.PackageInfoFlags.of(0))
        } else {
            pm.getPackageInfo(pkgName, 0)
        }

        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pkgInfo.longVersionCode
        } else {
            pkgInfo.versionCode.toLong()
        }

        val installerPackage = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pm.getInstallSourceInfo(pkgName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                pm.getInstallerPackageName(pkgName)
            }
        }.getOrNull()

        val isDebuggable = (pkgInfo.applicationInfo?.flags?.and(ApplicationInfo.FLAG_DEBUGGABLE)) != 0

        val targetSdk = pkgInfo.applicationInfo?.targetSdkVersion
        val minSdk    = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) pkgInfo.applicationInfo?.minSdkVersion
        else runCatching {
            val ai = pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
            val metaData = ai.metaData
            metaData?.getInt("minSdkVersion")
        }.getOrNull()

        return AppInfo(
            packageName = pkgName,
            versionName = pkgInfo.versionName ?: "unknown",
            versionCode = versionCode,
            installerPackage = installerPackage,
            debuggable = isDebuggable,
            targetSdkVersion = targetSdk,
            minSdkVersion = minSdk ?: 0,
            firstInstallTime = pkgInfo.firstInstallTime,
            lastUpdateTime = pkgInfo.lastUpdateTime
        )
    }

    // ─────────────────────────────────────────────
    //  Aggregate
    // ─────────────────────────────────────────────

    override fun getDeviceInfo(): DeviceInfo = DeviceInfo(
        deviceId          = getDeviceId(),
        osVersion         = getOsVersion(),
        osApiLevel        = getOsApiLevel(),
        osCodename        = getOsCodename(),
        securityPatchLevel = getSecurityPatchLevel(),
        isPreviewBuild    = isPreviewBuild(),
        manufacturer      = getManufacturer(),
        deviceName        = getDeviceName(),
        model             = getModel(),
        board             = getBoard(),
        brand             = getBrand(),
        hardware          = getHardware(),
        buildFingerprint  = getBuildFingerprint(),
        buildType         = getBuildType(),
        buildId           = getBuildId(),
        isRooted          = isRooted(),
        isEmulator        = isEmulator(),
        deviceType        = getDeviceType(),
        screenInfo        = getScreenInfo(),
        hardwareInfo      = getHardwareInfo(),
        networkInfo       = getNetworkInfo(),
        appInfo           = getAppInfo()
    )

    override fun getDeviceInfoAsMap(): Map<String, String> {
        val info   = getDeviceInfo()
        val screen = info.screenInfo
        val hw     = info.hardwareInfo
        val net    = info.networkInfo
        val app    = info.appInfo

        return buildMap {
            // Identity
            put("device_id",               info.deviceId)
            // OS
            put("os_version",              info.osVersion)
            put("os_api_level",            info.osApiLevel.toString())
            put("os_codename",             info.osCodename)
            put("security_patch",          info.securityPatchLevel)
            put("is_preview_build",        info.isPreviewBuild.toString())
            // Hardware / Build
            put("manufacturer",            info.manufacturer)
            put("device_name",             info.deviceName)
            put("model",                   info.model)
            put("board",                   info.board)
            put("brand",                   info.brand)
            put("hardware",                info.hardware)
            put("build_fingerprint",       info.buildFingerprint)
            put("build_type",              info.buildType)
            put("build_id",                info.buildId)
            put("is_rooted",               info.isRooted.toString())
            put("is_emulator",             info.isEmulator.toString())
            // Form factor
            put("device_type",             info.deviceType.label)
            // Screen
            put("screen_width_px",         screen.widthPx.toString())
            put("screen_height_px",        screen.heightPx.toString())
            put("screen_width_dp",         screen.widthDp.toString())
            put("screen_height_dp",        screen.heightDp.toString())
            put("screen_density_dpi",      screen.densityDpi.toString())
            put("screen_density_bucket",   screen.densityBucket.name)
            put("screen_refresh_rate",     screen.refreshRate.toString())
            put("screen_hdr",              screen.hdrCapable.toString())
            put("screen_round",            screen.roundScreen.toString())
            // CPU / Memory
            put("cpu_abi",                 hw.cpuAbi)
            put("cpu_cores",               hw.cpuCores.toString())
            put("cpu_freq_khz",            hw.cpuFrequencyKhz.toString())
            put("ram_total_bytes",         hw.totalRamBytes.toString())
            put("ram_available_bytes",     hw.availableRamBytes.toString())
            put("low_ram_device",          hw.lowRamDevice.toString())
            put("storage_total_bytes",     hw.totalStorageBytes.toString())
            put("storage_available_bytes", hw.availableStorageBytes.toString())
            // Network
            put("network_connected",       net.isConnected.toString())
            put("network_type",            net.connectionType.name)
            put("network_metered",         net.isMetered.toString())
            put("network_vpn",             net.isVpnActive.toString())
            put("network_operator",        net.networkOperator)
            put("network_country_iso",     net.networkCountryIso)
            put("sim_count",               net.simCount.toString())
            // App
            put("app_package",             app.packageName)
            put("app_version_name",        app.versionName)
            put("app_version_code",        app.versionCode.toString())
            put("app_installer",           app.installerPackage ?: "sideload/unknown")
            put("app_debuggable",          app.debuggable.toString())
            put("app_target_sdk",          app.targetSdkVersion.toString())
            put("app_min_sdk",             app.minSdkVersion.toString())
            put("app_first_install",       app.firstInstallTime.toString())
            put("app_last_update",         app.lastUpdateTime.toString())
            // Meta
            put("snapshot_timestamp",      info.snapshotTimestamp.toString())
        }
    }

    // ─────────────────────────────────────────────
    //  Private helpers
    // ─────────────────────────────────────────────

    private fun packageExists(pm: PackageManager, packageName: String): Boolean = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(packageName, 0)
        }
        true
    }.getOrDefault(false)

    private fun String.capitalize(): String =
        this.replaceFirstChar { it.uppercase() }
}