package com.application.echo.core.common.manager.battery

import com.application.echo.core.common.manager.model.BatteryChargingState
import com.application.echo.core.common.manager.model.BatteryInfo
import com.application.echo.core.common.manager.model.BatteryPowerProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for managing battery-related information and state.
 * Provides access to battery info, charging state, power profile, and low power mode.
 */
interface BatteryCoreManager {

    /**
     * Current battery information.
     */
    val batteryInfo: BatteryInfo

    /**
     * Flow of battery information updates.
     */
    val batteryInfoFlow: StateFlow<BatteryInfo>

    /**
     * Current charging state of the battery.
     */
    val chargingState: BatteryChargingState

    /**
     * Flow of charging state updates.
     */
    val chargingStateFlow: StateFlow<BatteryChargingState>

    /**
     * Current power profile of the battery.
     */
    val powerProfile: BatteryPowerProfile

    /**
     * Flow of power profile updates.
     */
    val powerProfileFlow: StateFlow<BatteryPowerProfile>

    /**
     * Indicates whether low power mode is enabled.
     */
    val isLowPowerEnabled: Boolean

    /**
     * Flow of low power mode state updates.
     */
    val lowPowerModeFlow: StateFlow<Boolean>

    /**
     * Disposes resources used by the manager.
     */
    fun dispose()
}