package com.application.echo.core.api.session

import com.application.echo.core.api.common.ApiConstants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp interceptor that attaches session and device headers to every request.
 *
 * Reads values from [SessionProvider] at call-time so it always reflects
 * the latest session state (e.g. after login or token refresh).
 *
 * **Headers attached:**
 * - `X-Session-ID` / `X-Session-Token` (if authenticated)
 * - `X-Device-ID`, `X-Device-Name`, `X-Device-Type`, `X-Device-Platform`
 * - `X-Device-OS`, `X-Device-OS-Version`, `X-Device-Model`, `X-Device-Manufacturer`
 */
internal class SessionHeaderInterceptor @Inject constructor(
    private val sessionProvider: SessionProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        // Session headers (only when authenticated)
        sessionProvider.sessionId?.let {
            builder.header(ApiConstants.HEADER_SESSION_ID, it)
        }
        sessionProvider.sessionToken?.let {
            builder.header(ApiConstants.HEADER_SESSION_TOKEN, it)
        }

        // Device headers (always)
        val device = sessionProvider.deviceInfo
        builder.header(ApiConstants.HEADER_DEVICE_ID, device.deviceId)
        builder.header(ApiConstants.HEADER_DEVICE_NAME, device.deviceName)
        builder.header(ApiConstants.HEADER_DEVICE_TYPE, device.deviceType)
        builder.header(ApiConstants.HEADER_DEVICE_PLATFORM, device.platform)
        builder.header(ApiConstants.HEADER_DEVICE_OS, device.platform)
        builder.header(ApiConstants.HEADER_DEVICE_OS_VERSION, device.osVersion)
        builder.header(ApiConstants.HEADER_DEVICE_MODEL, device.model)
        builder.header(ApiConstants.HEADER_DEVICE_MANUFACTURER, device.manufacturer)

        return chain.proceed(builder.build())
    }
}
