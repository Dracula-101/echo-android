package com.application.echo.core.common.model

/**
 * Network connectivity snapshot.
 *
 * @param isConnected       Whether any network is reachable
 * @param connectionType    Active connection type
 * @param isMetered         Whether the connection is metered
 * @param isVpnActive       Whether a VPN is active
 * @param networkOperator   Carrier name (e.g. "T-Mobile"), empty if unknown
 * @param networkCountryIso ISO 3166-1 country of SIM/cell, empty if unknown
 * @param simCount          Number of SIM slots detected
 */
data class NetworkInfo(
    val isConnected: Boolean,
    val connectionType: ConnectionType,
    val isMetered: Boolean,
    val isVpnActive: Boolean,
    val networkOperator: String,
    val networkCountryIso: String,
    val simCount: Int
)