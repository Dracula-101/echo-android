package com.application.echo.core.network.client

import com.application.echo.core.network.util.TimeoutDefaults
import com.application.echo.core.network.util.TimeoutValue

/**
 * Configuration that drives the creation of OkHttp + Retrofit instances.
 *
 * Designed to be created once (typically in a Hilt module) and shared
 * across the application lifetime.
 *
 * @property baseUrl The root URL of the API (must end with `/`).
 * @property isDebug `true` to enable verbose HTTP body logging.
 * @property connectTimeout Connect timeout.
 * @property readTimeout Read timeout.
 * @property writeTimeout Write timeout.
 */
data class HttpClientConfig(
    val baseUrl: String,
    val isDebug: Boolean = false,
    val connectTimeout: TimeoutValue = TimeoutDefaults.CONNECT,
    val readTimeout: TimeoutValue = TimeoutDefaults.READ,
    val writeTimeout: TimeoutValue = TimeoutDefaults.WRITE,
)
