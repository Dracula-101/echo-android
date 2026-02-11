package com.application.echo.core.websocket.di

import com.application.echo.core.websocket.channel.BufferedMessageChannel
import com.application.echo.core.websocket.channel.MessageChannel
import com.application.echo.core.websocket.config.WebSocketConfig
import com.application.echo.core.websocket.connection.OkHttpWebSocketConnection
import com.application.echo.core.websocket.connection.WebSocketConnection
import com.application.echo.core.websocket.heartbeat.HeartbeatManager
import com.application.echo.core.websocket.heartbeat.TickerHeartbeatManager
import com.application.echo.core.websocket.interceptor.AuthTokenInterceptor
import com.application.echo.core.websocket.interceptor.LoggingInterceptor
import com.application.echo.core.websocket.interceptor.MessageInterceptor
import com.application.echo.core.websocket.interceptor.TimestampInterceptor
import com.application.echo.core.websocket.message.GsonMessageDeserializer
import com.application.echo.core.websocket.message.GsonMessageSerializer
import com.application.echo.core.websocket.message.MessageDeserializer
import com.application.echo.core.websocket.message.MessageSerializer
import com.application.echo.core.websocket.qualifier.WebSocketOkHttp
import com.application.echo.core.websocket.reconnect.ExponentialBackoffStrategy
import com.application.echo.core.websocket.reconnect.ReconnectionStrategy
import com.application.echo.core.websocket.session.EchoWebSocketSession
import com.application.echo.core.websocket.session.WebSocketSession
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt bindings for interface → implementation mappings.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class WebSocketBindsModule {

    @Binds
    @Singleton
    abstract fun bindWebSocketConnection(
        impl: OkHttpWebSocketConnection,
    ): WebSocketConnection

    @Binds
    @Singleton
    abstract fun bindReconnectionStrategy(
        impl: ExponentialBackoffStrategy,
    ): ReconnectionStrategy

    @Binds
    @Singleton
    abstract fun bindHeartbeatManager(
        impl: TickerHeartbeatManager,
    ): HeartbeatManager

    @Binds
    @Singleton
    abstract fun bindMessageSerializer(
        impl: GsonMessageSerializer,
    ): MessageSerializer

    @Binds
    @Singleton
    abstract fun bindMessageDeserializer(
        impl: GsonMessageDeserializer,
    ): MessageDeserializer

    @Binds
    @Singleton
    abstract fun bindMessageChannel(
        impl: BufferedMessageChannel,
    ): MessageChannel

    @Binds
    @Singleton
    abstract fun bindWebSocketSession(
        impl: EchoWebSocketSession,
    ): WebSocketSession

    // ──────────────── Interceptor Multi-Bindings ────────────────

    @Binds
    @IntoSet
    abstract fun bindLoggingInterceptor(
        impl: LoggingInterceptor,
    ): MessageInterceptor

    @Binds
    @IntoSet
    abstract fun bindTimestampInterceptor(
        impl: TimestampInterceptor,
    ): MessageInterceptor

    @Binds
    @IntoSet
    abstract fun bindAuthTokenInterceptor(
        impl: AuthTokenInterceptor,
    ): MessageInterceptor
}

/**
 * Hilt providers for constructions that require [internal] implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object WebSocketProvidesModule {

    @Provides
    @Singleton
    @WebSocketOkHttp
    fun provideWebSocketOkHttpClient(config: WebSocketConfig): OkHttpClient =
        OkHttpClient.Builder()
            .pingInterval(config.pingIntervalMs, TimeUnit.MILLISECONDS)
            .connectTimeout(config.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(config.writeTimeoutMs, TimeUnit.MILLISECONDS)
            .build()
}
