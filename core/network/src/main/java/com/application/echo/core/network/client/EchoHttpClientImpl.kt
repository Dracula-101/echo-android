package com.application.echo.core.network.client

import com.application.echo.core.network.adapter.NetworkResponseCallAdapterFactory
import com.application.echo.core.network.interceptor.AuthInterceptor
import com.application.echo.core.network.interceptor.AuthTokenProvider
import com.application.echo.core.network.interceptor.LoggingInterceptorFactory
import com.application.echo.core.network.interceptor.RequestHeaderInterceptor
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Default implementation of [EchoHttpClient].
 *
 * Builds two OkHttp clients:
 * 1. **unauthenticatedOkHttp** — common headers + logging.
 * 2. **authenticatedOkHttp** — same as above + [AuthInterceptor].
 *
 * Both share the same Retrofit configuration (GSON, call adapter, base URL).
 */
internal class EchoHttpClientImpl(
    private val config: HttpClientConfig,
    private val gson: Gson,
    private val authTokenProvider: AuthTokenProvider,
) : EchoHttpClient {

    // ──────────────── Interceptors ────────────────

    private val requestHeaderInterceptor by lazy { RequestHeaderInterceptor() }

    private val authInterceptor by lazy { AuthInterceptor(authTokenProvider) }

    private val loggingInterceptor by lazy {
        LoggingInterceptorFactory.create(logBody = config.isDebug)
    }

    // ──────────────── OkHttp Clients ────────────────

    private val baseOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(config.connectTimeout.duration, config.connectTimeout.unit)
            .readTimeout(config.readTimeout.duration, config.readTimeout.unit)
            .writeTimeout(config.writeTimeout.duration, config.writeTimeout.unit)
            .addInterceptor(requestHeaderInterceptor)
            .build()
    }

    private val unauthenticatedOkHttp: OkHttpClient by lazy {
        baseOkHttpClient.newBuilder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val authenticatedOkHttp: OkHttpClient by lazy {
        baseOkHttpClient.newBuilder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // ──────────────── Retrofit Builder ────────────────

    private val baseRetrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(NetworkResponseCallAdapterFactory(gson))
    }

    // ──────────────── Public API ────────────────

    override val unauthenticated: Retrofit by lazy {
        baseRetrofitBuilder
            .client(unauthenticatedOkHttp)
            .build()
    }

    override val authenticated: Retrofit by lazy {
        baseRetrofitBuilder
            .client(authenticatedOkHttp)
            .build()
    }
}
