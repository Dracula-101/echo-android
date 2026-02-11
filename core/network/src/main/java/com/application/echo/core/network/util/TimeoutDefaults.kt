package com.application.echo.core.network.util

import java.util.concurrent.TimeUnit

/**
 * Default timeout values used by the OkHttp client.
 *
 * Override per-client via [HttpClientConfig] if a specific
 * endpoint needs longer or shorter windows.
 */
object TimeoutDefaults {

    val CONNECT = TimeoutValue(30, TimeUnit.SECONDS)
    val READ = TimeoutValue(30, TimeUnit.SECONDS)
    val WRITE = TimeoutValue(30, TimeUnit.SECONDS)

    /** Longer timeouts for file uploads. */
    val UPLOAD_READ = TimeoutValue(120, TimeUnit.SECONDS)
    val UPLOAD_WRITE = TimeoutValue(120, TimeUnit.SECONDS)
}

data class TimeoutValue(
    val duration: Long,
    val unit: TimeUnit,
)
