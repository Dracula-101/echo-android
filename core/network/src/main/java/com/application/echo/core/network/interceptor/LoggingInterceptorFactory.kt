package com.application.echo.core.network.interceptor

import com.application.echo.core.network.util.HeaderConstants
import okhttp3.Interceptor
import okhttp3.MultipartBody
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

    fun create(logBody: Boolean): Interceptor {
        val logger = HttpLoggingInterceptor { message ->
            Timber.tag(TAG).d(message.chunked(4000).joinToString("\n"))
        }.apply {
            redactHeader(HeaderConstants.AUTHORIZATION)
        }

        return Interceptor { chain ->
            val isMultipart = chain.request().body is MultipartBody
            logger.level = when {
                isMultipart -> HttpLoggingInterceptor.Level.HEADERS
                logBody     -> HttpLoggingInterceptor.Level.BODY
                else        -> HttpLoggingInterceptor.Level.BASIC
            }
            logger.intercept(chain)
        }
    }
}
