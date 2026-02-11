package com.application.echo.core.api.di

import com.application.echo.core.api.auth.AuthApiService
import com.application.echo.core.api.auth.AuthRepository
import com.application.echo.core.api.auth.AuthRepositoryImpl
import com.application.echo.core.api.health.HealthRepository
import com.application.echo.core.api.health.HealthRepositoryImpl
import com.application.echo.core.api.media.MediaApiService
import com.application.echo.core.api.media.MediaRepository
import com.application.echo.core.api.media.MediaRepositoryImpl
import com.application.echo.core.api.message.MessageApiService
import com.application.echo.core.api.message.MessageRepository
import com.application.echo.core.api.message.MessageRepositoryImpl
import com.application.echo.core.api.session.SessionHeaderInterceptor
import com.application.echo.core.api.session.SessionProvider
import com.application.echo.core.api.user.UserApiService
import com.application.echo.core.api.user.UserRepository
import com.application.echo.core.api.user.UserRepositoryImpl
import com.application.echo.core.network.client.EchoHttpClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import javax.inject.Singleton

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Binds (interface → internal impl)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ApiBindsModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl,
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        impl: MediaRepositoryImpl,
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        impl: MessageRepositoryImpl,
    ): MessageRepository

    @Binds
    @Singleton
    abstract fun bindHealthRepository(
        impl: HealthRepositoryImpl,
    ): HealthRepository
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Provides (Retrofit service creation + helpers)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Module
@InstallIn(SingletonComponent::class)
internal object ApiProvidesModule {

    /**
     * Auth endpoints are public (login, register, refresh) — use [EchoHttpClient.unauthenticated].
     */
    @Provides
    @Singleton
    fun provideAuthApiService(
        client: EchoHttpClient,
    ): AuthApiService = client.unauthenticated.create(AuthApiService::class.java)

    /**
     * User endpoints require a Bearer token — use [EchoHttpClient.authenticated].
     */
    @Provides
    @Singleton
    fun provideUserApiService(
        client: EchoHttpClient,
    ): UserApiService = client.authenticated.create(UserApiService::class.java)

    /**
     * Media endpoints require a Bearer token — use [EchoHttpClient.authenticated].
     */
    @Provides
    @Singleton
    fun provideMediaApiService(
        client: EchoHttpClient,
    ): MediaApiService = client.authenticated.create(MediaApiService::class.java)

    /**
     * Message endpoints require a Bearer token — use [EchoHttpClient.authenticated].
     */
    @Provides
    @Singleton
    fun provideMessageApiService(
        client: EchoHttpClient,
    ): MessageApiService = client.authenticated.create(MessageApiService::class.java)

    /**
     * Session header interceptor — attaches X-Session-ID, X-Session-Token,
     * and X-Device-* headers to every request.
     *
     * The app module must provide a [SessionProvider] binding for this to work.
     * Add this interceptor to your OkHttpClient via [HttpClientConfig].
     */
    @Provides
    @Singleton
    @SessionInterceptor
    fun provideSessionHeaderInterceptor(
        sessionProvider: SessionProvider,
    ): Interceptor = SessionHeaderInterceptor(sessionProvider)
}
