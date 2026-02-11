package com.application.echo.core.network.interceptor

import com.application.echo.core.network.util.HeaderConstants
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Attaches standard request headers to every outgoing call.
 *
 * Headers added:
 * - `Accept: application/json`
 * - `Content-Type: application/json`
 * - `X-Platform: Android`
 *
 * Additional headers can be passed at construction time.
 */
internal class RequestHeaderInterceptor(
    private val additionalHeaders: Map<String, String> = emptyMap(),
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .header(HeaderConstants.ACCEPT, HeaderConstants.APPLICATION_JSON)
            .header(HeaderConstants.CONTENT_TYPE, HeaderConstants.APPLICATION_JSON)
            .header(HeaderConstants.X_PLATFORM, HeaderConstants.ANDROID)

        additionalHeaders.forEach { (key, value) ->
            builder.header(key, value)
        }

        return chain.proceed(builder.build())
    }
}
