package com.application.echo.core.common.manager.model

data class BatteryPowerProfile(
    val isPowerSaveActive: Boolean,
    val estimatedMinutesRemaining: Int = -1,
    val consumptionRate: Float = 0f
) {
    val batteryLifeCategory: BatteryLifeCategory = when {
        estimatedMinutesRemaining <= 60 -> BatteryLifeCategory.CRITICAL
        estimatedMinutesRemaining <= 180 -> BatteryLifeCategory.LOW
        estimatedMinutesRemaining <= 480 -> BatteryLifeCategory.MODERATE
        else -> BatteryLifeCategory.GOOD
    }
}