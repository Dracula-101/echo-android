package com.application.echo.core.network.di

import com.application.echo.core.network.client.EchoHttpClient
import com.application.echo.core.network.client.EchoHttpClientImpl
import com.application.echo.core.network.client.HttpClientConfig
import com.application.echo.core.network.interceptor.AuthTokenProvider
import com.application.echo.core.network.monitor.ConnectivityManagerNetworkMonitor
import com.application.echo.core.network.monitor.NetworkMonitor
import com.application.echo.core.network.qualifier.Authenticated
import com.application.echo.core.network.qualifier.Unauthenticated
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkBindsModule {

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        impl: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkProvidesModule {

    @Provides
    @Singleton
    fun provideEchoHttpClient(
        config: HttpClientConfig,
        gson: Gson,
        authTokenProvider: AuthTokenProvider,
    ): EchoHttpClient = EchoHttpClientImpl(
        config = config,
        gson = gson,
        authTokenProvider = authTokenProvider,
    )

    @Provides
    @Singleton
    @Unauthenticated
    fun provideUnauthenticatedRetrofit(client: EchoHttpClient): Retrofit =
        client.unauthenticated

    @Provides
    @Singleton
    @Authenticated
    fun provideAuthenticatedRetrofit(client: EchoHttpClient): Retrofit =
        client.authenticated
}
