package com.application.echo.core.network.monitor

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production [NetworkMonitor] backed by [ConnectivityManager].
 *
 * Registers a system-level callback for network state changes and
 * emits the latest value as a cold [Flow].
 */
@Singleton
internal class ConnectivityManagerNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) : NetworkMonitor {

    private val connectivityManager: ConnectivityManager? =
        context.getSystemService()

    override val isCurrentlyOnline: Boolean
        get() {
            val manager = connectivityManager ?: return false
            val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
            return capabilities.hasInternetCapability()
        }

    override val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {

            private val connectedNetworks = mutableSetOf<Network>()

            override fun onAvailable(network: Network) {
                connectedNetworks += network
                trySend(true)
            }

            override fun onLost(network: Network) {
                connectedNetworks -= network
                trySend(connectedNetworks.isNotEmpty())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                if (networkCapabilities.hasInternetCapability()) {
                    connectedNetworks += network
                } else {
                    connectedNetworks -= network
                }
                trySend(connectedNetworks.isNotEmpty())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager?.registerNetworkCallback(request, callback)

        // Emit the initial state immediately.
        trySend(isCurrentlyOnline)

        awaitClose {
            connectivityManager?.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged().conflate()

    private fun NetworkCapabilities?.hasInternetCapability(): Boolean =
        this?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}
