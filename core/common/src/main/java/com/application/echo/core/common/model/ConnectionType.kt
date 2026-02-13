package com.application.echo.core.common.model

/**
 * Active network connection type.
 */
enum class ConnectionType {
    WIFI,
    CELLULAR_2G,
    CELLULAR_3G,
    CELLULAR_4G_LTE,
    CELLULAR_5G,
    ETHERNET,
    BLUETOOTH,
    VPN,
    NONE,
    UNKNOWN
}