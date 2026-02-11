package com.application.echo.core.common.manager.model

data class BatteryChargingState(
    val isPluggedIn: Boolean,
    val chargingType: BatteryChargingType,
    val estimatedMinutesToFull: Int = -1,
    val chargingSpeed: BatteryChargingSpeed = BatteryChargingSpeed.UNKNOWN
) {
    val isActivelyCharging: Boolean = isPluggedIn && chargingType != BatteryChargingType.NONE
}