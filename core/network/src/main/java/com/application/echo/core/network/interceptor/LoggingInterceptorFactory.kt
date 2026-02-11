package com.application.echo.core.network.interceptor

import com.application.echo.core.network.util.HeaderConstants
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * Creates a configured [HttpLoggingInterceptor] for the network module.
 *
 * - Logs to Timber with a dedicated tag.
 * - Redacts the Authorization header value.
 * - Level is configurable (BODY for debug, BASIC for production).
 */
internal object LoggingInterceptorFactory {

    private const val TAG = "EchoHttp"

    fun create(logBody: Boolean): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Timber.tag(TAG).d(message)
        }.apply {
            redactHeader(HeaderConstants.AUTHORIZATION)
            level = if (logBody) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }
    }
}
