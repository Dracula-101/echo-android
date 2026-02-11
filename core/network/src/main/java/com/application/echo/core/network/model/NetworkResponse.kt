package com.application.echo.core.network.model

/**
 * Sealed wrapper for every API response.
 *
 * By design, every call through [NetworkResponseCallAdapterFactory]
 * returns a [NetworkResponse] instead of throwing. This forces callers
 * to handle both paths exhaustively.
 *
 * ```
 * when (val response = api.getUser()) {
 *     is NetworkResponse.Success -> showUser(response.data)
 *     is NetworkResponse.Error   -> showError(response.error)
 * }
 * ```
 */
sealed class NetworkResponse<out T> {

    /**
     * The request completed and the server indicated success (`meta.success == true`).
     */
    data class Success<T>(
        val meta: Meta,
        val message: String,
        val data: T,
    ) : NetworkResponse<T>()

    /**
     * The request failed — either server-side, network-level, or parse-level.
     */
    data class Error(
        val meta: Meta,
        val error: NetworkException,
    ) : NetworkResponse<Nothing>()
}
