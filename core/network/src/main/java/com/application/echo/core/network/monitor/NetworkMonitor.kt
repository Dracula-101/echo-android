package com.application.echo.core.network.monitor

import kotlinx.coroutines.flow.Flow

/**
 * Observes the device's network connectivity state.
 *
 * Implementations should emit `true` when the device has internet
 * access and `false` when offline.
 */
interface NetworkMonitor {

    /**
     * A [Flow] that emits `true` whenever the device is online.
     */
    val isOnline: Flow<Boolean>

    /**
     * Snapshot of the current connectivity state.
     */
    val isCurrentlyOnline: Boolean
}
