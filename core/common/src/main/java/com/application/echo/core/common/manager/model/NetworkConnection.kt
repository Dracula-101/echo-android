package com.application.echo.core.common.manager.model

/**
 * A representation of the current network connection.
 */
sealed class NetworkConnection {
    /**
     * Currently not connected to the internet.
     */
    data object None : NetworkConnection()

    /**
     * Currently connected to the internet via WiFi with a signal [strength] indication.
     */
    data class Wifi(
        val strength: NetworkSignalStrength,
    ) : NetworkConnection()

    /**
     * Currently connected to the internet via cellular connection.
     */
    data object Cellular : NetworkConnection()

    /**
     * Currently connected to the internet via an unknown connection.
     */
    data object Other : NetworkConnection()

    /**
     * Currently connected to the internet via VPN with an underlying [connection] type.
     * This indicates the device is using a VPN service, which is important for
     * content filtering and security policies in social media applications.
     */
    data class Vpn(
        val underlyingConnection: NetworkConnection,
    ) : NetworkConnection()
}

/**
 * Extension property to easily check if the current connection is using VPN.
 */
val NetworkConnection.isVpnActive: Boolean
    get() = this is NetworkConnection.Vpn

/**
 * Extension property to get the underlying connection type when VPN is active.
 * Returns the connection itself if VPN is not active.
 */
val NetworkConnection.baseConnection: NetworkConnection
    get() = when (this) {
        is NetworkConnection.Vpn -> underlyingConnection
        else -> this
    }