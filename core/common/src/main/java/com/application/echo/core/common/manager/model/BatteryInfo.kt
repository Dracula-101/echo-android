package com.application.echo.core.common.manager.model

data class BatteryInfo(
    val percentage: Int,
    val healthStatus: BatteryHealthStatus,
    val temperatureCelsius: Float,
    val voltageMillivolts: Int,
    val technology: String = "Unknown"
) {
    val isHealthy: Boolean = healthStatus == BatteryHealthStatus.GOOD
    val isOverheating: Boolean = temperatureCelsius > 45f
    val isCritical: Boolean = percentage <= 10
    val isLow: Boolean = percentage <= 20
}