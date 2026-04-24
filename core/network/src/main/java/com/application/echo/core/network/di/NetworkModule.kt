package com.application.echo.core.network.di

import android.se.omapi.Session
import com.application.echo.core.network.client.EchoHttpClient
import com.application.echo.core.network.client.EchoHttpClientImpl
import com.application.echo.core.network.client.HttpClientConfig
import com.application.echo.core.network.provider.AuthTokenProvider
import com.application.echo.core.network.provider.DeviceInfoProvider
import com.application.echo.core.network.monitor.ConnectivityManagerNetworkMonitor
import com.application.echo.core.network.monitor.NetworkMonitor
import com.application.echo.core.network.provider.SessionProvider
import com.application.echo.core.network.qualifier.Authenticated
import com.application.echo.core.network.qualifier.Unauthenticated
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Authenticator
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
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .serializeNulls()
            .setPrettyPrinting()
            .create()
    }



    @Provides
    @Singleton
    fun provideEchoHttpClient(
        config: HttpClientConfig,
        gson: Gson,
        authTokenProvider: AuthTokenProvider,
        deviceInfoProvider: DeviceInfoProvider,
        sessionProvider: SessionProvider,
        authenticator: Authenticator,
    ): EchoHttpClient = EchoHttpClientImpl(
        config = config,
        gson = gson,
        authTokenProvider = authTokenProvider,
        deviceInfoProvider = deviceInfoProvider,
        sessionProvider = sessionProvider,
        authenticator = authenticator,
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
