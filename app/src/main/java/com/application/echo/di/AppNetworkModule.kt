package com.application.echo.di

import com.application.echo.BuildConfig
import com.application.echo.core.network.client.EchoHttpClient
import com.application.echo.core.network.client.HttpClientConfig
import com.application.echo.core.network.qualifier.Authenticated
import com.application.echo.core.network.qualifier.Unauthenticated
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

     @Provides
     @Singleton
     fun provideHttpClientConfig(): HttpClientConfig {
         return HttpClientConfig(
             baseUrl = BuildConfig.BACKEND_URL,
             isDebug = BuildConfig.DEBUG,
         )
     }
}