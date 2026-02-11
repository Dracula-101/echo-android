package com.application.echo.core.common.manager.network

import com.application.echo.core.common.manager.model.NetworkConnection
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager to detect and handle changes to network connectivity and VPN status.
 * This interface provides comprehensive network monitoring capabilities for social media applications.
 */
interface NetworkManager {

    /**
     * Returns `true` if the application has a network connection and access to the Internet is
     * available.
     */
    val isNetworkConnected: Boolean

    /**
     * Emits `true` when the application has a network connection and access to the Internet is
     * available.
     */
    val isNetworkConnectedFlow: StateFlow<Boolean>

    /**
     * Returns the current network connection.
     */
    val networkConnection: NetworkConnection

    /**
     * Emits the current [NetworkConnection] indicating what type of network the app is currently
     * using to connect to the internet.
     */
    val networkConnectionFlow: StateFlow<NetworkConnection>

    /**
     * Releases any resources held by the manager.
     * Should be called when the manager is no longer needed.
     */
    fun release()
}