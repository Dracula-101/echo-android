package com.application.echo.core.network.client

import retrofit2.Retrofit

/**
 * Public API surface of the Echo network module.
 *
 * Provides pre-configured [Retrofit] instances that any module can
 * use to create typed API service interfaces.
 *
 * ```kotlin
 * @Provides fun provideUserApi(client: EchoHttpClient): UserApi =
 *     client.authenticated.create(UserApi::class.java)
 * ```
 */
interface EchoHttpClient {

    /**
     * A [Retrofit] instance configured **without** an Authorization header.
     *
     * Use for public endpoints (login, registration, password reset).
     */
    val unauthenticated: Retrofit

    /**
     * A [Retrofit] instance configured **with** the [AuthInterceptor].
     *
     * Use for endpoints that require a Bearer token.
     */
    val authenticated: Retrofit
}
