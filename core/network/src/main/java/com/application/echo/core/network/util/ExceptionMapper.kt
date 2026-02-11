package com.application.echo.core.network.util

import com.application.echo.core.network.model.NetworkException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import retrofit2.HttpException

/**
 * Maps raw [Throwable]s into typed [NetworkException] subtypes.
 *
 * Used by the call adapter layer to ensure every failure is categorised.
 */
fun Throwable.toNetworkException(): NetworkException = when (this) {
    is SocketTimeoutException -> NetworkException.Timeout(this)
    is UnknownHostException -> NetworkException.Network(this)
    is SSLException -> NetworkException.Network(this)
    is IOException -> NetworkException.Network(this)
    is HttpException -> NetworkException.Http(
        throwable = this,
        message = "HTTP ${code()} error",
    )
    else -> NetworkException.Unknown(this)
}
