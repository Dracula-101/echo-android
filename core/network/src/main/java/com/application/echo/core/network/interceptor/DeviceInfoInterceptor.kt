package com.application.echo.core.network.interceptor

import com.application.echo.core.network.provider.DeviceInfoProvider
import com.application.echo.core.network.util.HeaderConstants
import okhttp3.Interceptor
import okhttp3.Response

internal class DeviceInfoInterceptor(
    private val deviceInfoProvider: DeviceInfoProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
        val device = deviceInfoProvider.getDeviceInfo()
        builder.header(HeaderConstants.DEVICE_ID, device.deviceId)
        builder.header(HeaderConstants.DEVICE_NAME, device.deviceName)
        builder.header(HeaderConstants.DEVICE_TYPE, device.deviceType)
        builder.header(HeaderConstants.DEVICE_PLATFORM, device.platform)
        builder.header(HeaderConstants.DEVICE_OS, device.platform)
        builder.header(HeaderConstants.DEVICE_OS_VERSION, device.osVersion)
        builder.header(HeaderConstants.DEVICE_MODEL, device.model)
        builder.header(HeaderConstants.DEVICE_MANUFACTURER, device.manufacturer)

        return chain.proceed(builder.build())
    }
}