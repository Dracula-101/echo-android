package com.application.echo.core.network.qualifier

import javax.inject.Qualifier

/**
 * Qualifies an [okhttp3.OkHttpClient] or [retrofit2.Retrofit] instance
 * that does NOT include the [AuthInterceptor].
 *
 * Use for public endpoints (login, registration, health-check).
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Unauthenticated

/**
 * Qualifies an [okhttp3.OkHttpClient] or [retrofit2.Retrofit] instance
 * that DOES include the [AuthInterceptor].
 *
 * Use for endpoints that require a Bearer token.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Authenticated
