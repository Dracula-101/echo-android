package com.application.echo.di

import com.application.echo.BuildConfig
import com.application.echo.api.manager.AuthTokenManager
import com.application.echo.core.websocket.config.HeartbeatConfig
import com.application.echo.core.websocket.config.ReconnectionConfig
import com.application.echo.core.websocket.config.WebSocketConfig
import com.application.echo.core.websocket.interceptor.TokenProvider
import com.application.echo.features.auth.datasource.disk.AuthDiskSource
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
    fun provideWebSocketConfig(authDiskSource: AuthDiskSource): WebSocketConfig {
        return WebSocketConfig(
            url = BuildConfig.WS_URL,
            isDebug = BuildConfig.DEBUG,
            headers = LazyUserIdHeaders(authDiskSource),
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
        return HeartbeatConfig.DEFAULT
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

/**
 * A [Map] that lazily reads the current user ID from [AuthDiskSource]
 * each time its entries are iterated. This ensures the `X-User-ID`
 * header is always up-to-date when the WebSocket (re)connects.
 */
private class LazyUserIdHeaders(
    private val authDiskSource: AuthDiskSource,
) : AbstractMap<String, String>() {

    override val entries: Set<Map.Entry<String, String>>
        get() {
            val userId = authDiskSource.userState.userId
            return if (userId.isNotEmpty()) {
                setOf(entry(HEADER_USER_ID, userId))
            } else {
                emptySet()
            }
        }

    private fun entry(key: String, value: String): Map.Entry<String, String> =
        java.util.AbstractMap.SimpleImmutableEntry(key, value)

    private companion object {
        const val HEADER_USER_ID = "X-User-ID"
    }
}
