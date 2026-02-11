package com.application.echo.core.common.manager.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.application.echo.core.common.manager.dispatcher.DispatcherManager
import com.application.echo.core.common.manager.model.BatteryChargingSpeed
import com.application.echo.core.common.manager.model.BatteryChargingState
import com.application.echo.core.common.manager.model.BatteryChargingType
import com.application.echo.core.common.manager.model.BatteryHealthStatus
import com.application.echo.core.common.manager.model.BatteryInfo
import com.application.echo.core.common.manager.model.BatteryPowerProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import kotlin.math.roundToInt

// Light comments added to explain initialization and monitoring setup.
class BatteryCoreManagerImpl(
    private val context: Context,
    dispatcherManager: DispatcherManager
) : BatteryCoreManager {

    // Coroutine scope for background tasks.
    private val scope = CoroutineScope(dispatcherManager.io)

    // Broadcast receivers for battery and power monitoring.
    private val batteryReceiver = BatteryStatusReceiver()
    private val powerReceiver = PowerModeReceiver()

    // System services for battery and power management.
    private val batteryManager =
        context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    // State flows for battery info, charging state, power profile, and low power mode.
    private val _batteryInfo = MutableStateFlow(getCurrentBatteryInfo())
    private val _chargingState = MutableStateFlow(getCurrentChargingState())
    private val _lowPowerMode = MutableStateFlow(getPowerSaveMode())
    private val _powerProfile = MutableStateFlow(getCurrentPowerProfile())

    private var receiversRegistered = false

    // Initialization of monitoring setup.
    init {
        setupBatteryMonitoring()
        setupPowerMonitoring()
    }

    override val batteryInfo: BatteryInfo
        get() = _batteryInfo.value

    override val batteryInfoFlow: StateFlow<BatteryInfo> = _batteryInfo.asStateFlow()

    override val chargingState: BatteryChargingState
        get() = _chargingState.value

    override val chargingStateFlow: StateFlow<BatteryChargingState> = _chargingState.asStateFlow()

    override val powerProfile: BatteryPowerProfile
        get() = _powerProfile.value

    override val powerProfileFlow: StateFlow<BatteryPowerProfile> = _powerProfile.asStateFlow()

    override val isLowPowerEnabled: Boolean
        get() = _lowPowerMode.value

    override val lowPowerModeFlow: StateFlow<Boolean> = _lowPowerMode.asStateFlow()

    private fun setupBatteryMonitoring() {
        runCatching {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_CHANGED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
            context.registerReceiver(batteryReceiver, filter)
            receiversRegistered = true
        }.onFailure { e ->
            Timber.e(e, "Failed to register battery receiver")
        }
    }

    private fun setupPowerMonitoring() {
        runCatching {
            val powerFilter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
            ContextCompat.registerReceiver(
                context,
                powerReceiver,
                powerFilter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }.onFailure { e ->
            Timber.e(e, "Failed to register power receiver")
        }

        combine(_batteryInfo, _chargingState, _lowPowerMode) { battery, charging, lowPower ->
            calculatePowerProfile(battery, charging, lowPower)
        }.onEach { profile ->
            _powerProfile.value = profile
        }.launchIn(scope)
    }

     private fun getCurrentBatteryInfo(): BatteryInfo {
        val intent = getBatteryIntent() ?: return getDefaultBatteryInfo()

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percentage = if (level >= 0 && scale > 0) {
            ((level / scale.toFloat()) * 100).roundToInt()
        } else 0

        return BatteryInfo(
            percentage = percentage,
            healthStatus = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1).toHealthStatus(),
            temperatureCelsius = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f,
            voltageMillivolts = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0),
            technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"
        )
    }

    private fun getCurrentChargingState(): BatteryChargingState {
        val intent = getBatteryIntent() ?: return getDefaultChargingState()

        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val chargingType = plugged.toChargingType()
        val isPlugged = plugged != 0

        val estimatedTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && isPlugged) {
            batteryManager?.computeChargeTimeRemaining()?.let { millis ->
                (millis / 60000).toInt()
            } ?: -1
        } else -1

        return BatteryChargingState(
            isPluggedIn = isPlugged,
            chargingType = chargingType,
            estimatedMinutesToFull = estimatedTime,
            chargingSpeed = determineChargingSpeed(chargingType)
        )
    }

    private fun getCurrentPowerProfile(): BatteryPowerProfile {
        return calculatePowerProfile(
            _batteryInfo.value,
            _chargingState.value,
            _lowPowerMode.value
        )
    }

    private fun calculatePowerProfile(
        battery: BatteryInfo,
        charging: BatteryChargingState,
        lowPowerMode: Boolean
    ): BatteryPowerProfile {
        val estimatedMinutes = if (!charging.isActivelyCharging && battery.percentage > 0) {
            estimateBatteryLife(battery.percentage, lowPowerMode)
        } else -1

        return BatteryPowerProfile(
            isPowerSaveActive = lowPowerMode,
            estimatedMinutesRemaining = estimatedMinutes,
            consumptionRate = calculateConsumptionRate(lowPowerMode)
        )
    }

    private fun estimateBatteryLife(percentage: Int, lowPowerMode: Boolean): Int {
        val baseHours = when {
            lowPowerMode -> 1.5f
            else -> 1.0f
        }
        return (percentage * baseHours * 60 / 100).roundToInt()
    }

    private fun calculateConsumptionRate(lowPowerMode: Boolean): Float {
        return if (lowPowerMode) 0.6f else 1.0f
    }

    private fun getPowerSaveMode(): Boolean {
        return powerManager.isPowerSaveMode || powerManager.isDeviceIdleMode
    }

    private fun getBatteryIntent(): Intent? {
        return runCatching {
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        }.getOrNull()
    }

    private fun getDefaultBatteryInfo() = BatteryInfo(
        percentage = 0,
        healthStatus = BatteryHealthStatus.UNKNOWN,
        temperatureCelsius = 0f,
        voltageMillivolts = 0
    )

    private fun getDefaultChargingState() = BatteryChargingState(
        isPluggedIn = false,
        chargingType = BatteryChargingType.NONE
    )

    private fun Int.toHealthStatus(): BatteryHealthStatus = when (this) {
        BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealthStatus.GOOD
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealthStatus.OVERHEATING
        BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealthStatus.DEAD
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealthStatus.OVER_VOLTAGE
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealthStatus.FAILED
        BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealthStatus.COLD
        else -> BatteryHealthStatus.UNKNOWN
    }

    private fun Int.toChargingType(): BatteryChargingType = when (this) {
        BatteryManager.BATTERY_PLUGGED_AC -> BatteryChargingType.AC
        BatteryManager.BATTERY_PLUGGED_USB -> BatteryChargingType.USB
        BatteryManager.BATTERY_PLUGGED_WIRELESS -> BatteryChargingType.WIRELESS
        BatteryManager.BATTERY_PLUGGED_DOCK -> BatteryChargingType.DOCK
        else -> BatteryChargingType.NONE
    }

    private fun determineChargingSpeed(type: BatteryChargingType): BatteryChargingSpeed =
        when (type) {
            BatteryChargingType.AC -> BatteryChargingSpeed.FAST
            BatteryChargingType.USB -> BatteryChargingSpeed.NORMAL
            BatteryChargingType.WIRELESS -> BatteryChargingSpeed.SLOW
            BatteryChargingType.DOCK -> BatteryChargingSpeed.NORMAL
            BatteryChargingType.NONE -> BatteryChargingSpeed.UNKNOWN
        }

    private fun updateBatteryState() {
        _batteryInfo.value = getCurrentBatteryInfo()
        _chargingState.value = getCurrentChargingState()
        Timber.d("Battery updated: ${_batteryInfo.value}, Charging: ${_chargingState.value}")
    }

    private fun updatePowerMode() {
        _lowPowerMode.value = getPowerSaveMode()
        Timber.d("Power save mode: ${_lowPowerMode.value}")
    }

    override fun dispose() {
        runCatching {
            if (receiversRegistered) {
                context.unregisterReceiver(batteryReceiver)
                context.unregisterReceiver(powerReceiver)
                receiversRegistered = false
                Timber.d("Battery manager disposed")
            }
        }.onFailure { e ->
            Timber.e(e, "Error disposing battery manager")
        }
    }

    private inner class BatteryStatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_CHANGED,
                Intent.ACTION_POWER_CONNECTED,
                Intent.ACTION_POWER_DISCONNECTED -> updateBatteryState()
            }
        }
    }

    private inner class PowerModeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
                updatePowerMode()
            }
        }
    }
}