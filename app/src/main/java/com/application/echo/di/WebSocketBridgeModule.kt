package com.application.echo.di

import com.application.echo.BuildConfig
import com.application.echo.api.manager.AuthTokenManager
import com.application.echo.core.websocket.config.HeartbeatConfig
import com.application.echo.core.websocket.config.ReconnectionConfig
import com.application.echo.core.websocket.config.WebSocketConfig
import com.application.echo.core.websocket.interceptor.TokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebSocketBridgeModule {

    @Provides
    @Singleton
    fun provideWebSocketConfig(): WebSocketConfig {
        return WebSocketConfig(
            url = BuildConfig.WS_URL,
            isDebug = BuildConfig.DEBUG,
        )
    }

    @Provides
    @Singleton
    fun provideReconnectionConfig(): ReconnectionConfig {
        return ReconnectionConfig.DEFAULT
    }

    @Provides
    @Singleton
    fun provideHeartbeatConfig(): HeartbeatConfig {
        // Disabled — OkHttp's pingInterval (30s) handles transport-level keepalive.
        // The application-level heartbeat was sending raw binary frames that the
        // server rejected as invalid JSON.
        return HeartbeatConfig.NONE
    }

    @Provides
    @Singleton
    fun provideTokenProvider(authTokenManager: AuthTokenManager): TokenProvider {
        return object : TokenProvider {
            override fun getToken(): String? =
                authTokenManager.getLatestAuthTokenData()?.accessToken
        }
    }
}