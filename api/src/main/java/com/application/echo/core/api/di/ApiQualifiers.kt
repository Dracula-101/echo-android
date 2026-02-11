package com.application.echo.core.api.di

import javax.inject.Qualifier

/**
 * Qualifier for the [SessionHeaderInterceptor] that attaches
 * session and device metadata headers to every request.
 *
 * Usage in your app module's OkHttp configuration:
 * ```kotlin
 * @Provides
 * fun provideHttpClientConfig(
 *     @SessionInterceptor sessionInterceptor: Interceptor,
 * ): HttpClientConfig = HttpClientConfig(
 *     baseUrl = BuildConfig.BACKEND_URL,
 *     interceptors = listOf(sessionInterceptor),
 * )
 * ```
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SessionInterceptor
