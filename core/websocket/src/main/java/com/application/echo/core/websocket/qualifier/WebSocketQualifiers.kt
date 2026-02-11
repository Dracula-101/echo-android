package com.application.echo.core.websocket.qualifier

import javax.inject.Qualifier

/**
 * Qualifies an [OkHttpClient] instance configured specifically for
 * WebSocket connections (separate from the REST HTTP client).
 *
 * Use when injecting the OkHttp client that backs the WebSocket layer:
 * ```kotlin
 * @Inject constructor(
 *     @WebSocketOkHttp private val okHttpClient: OkHttpClient,
 * )
 * ```
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WebSocketOkHttp

/**
 * Qualifies a [CoroutineScope] dedicated to WebSocket operations.
 *
 * Use when injecting a scope that is tied to the WebSocket lifecycle:
 * ```kotlin
 * @Inject constructor(
 *     @WebSocketScope private val scope: CoroutineScope,
 * )
 * ```
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WebSocketScope
