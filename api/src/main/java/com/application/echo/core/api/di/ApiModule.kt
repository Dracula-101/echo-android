package com.application.echo.core.api.di

import android.content.SharedPreferences
import com.application.echo.core.api.auth.AuthApiRepository
import com.application.echo.core.api.auth.AuthApiService
import com.application.echo.core.api.auth.AuthApiRepositoryImpl
import com.application.echo.core.api.health.HealthRepository
import com.application.echo.core.api.health.HealthRepositoryImpl
import com.application.echo.core.api.manager.AuthTokenManager
import com.application.echo.core.api.manager.AuthTokenManagerImpl
import com.application.echo.core.api.media.MediaApiService
import com.application.echo.core.api.media.MediaApiRepository
import com.application.echo.core.api.media.MediaApiRepositoryImpl
import com.application.echo.core.api.message.MessageApiService
import com.application.echo.core.api.message.MessageApiRepository
import com.application.echo.core.api.message.MessageApiRepositoryImpl
import com.application.echo.core.api.session.SessionHeaderInterceptor
import com.application.echo.core.api.session.SessionProvider
import com.application.echo.core.api.user.UserApiService
import com.application.echo.core.api.user.UserApiRepository
import com.application.echo.core.api.user.UserApiRepositoryImpl
import com.application.echo.core.common.annotations.UnencryptedPreferences
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
        impl: AuthApiRepositoryImpl,
    ): AuthApiRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserApiRepositoryImpl,
    ): UserApiRepository

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        impl: MediaApiRepositoryImpl,
    ): MediaApiRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        impl: MessageApiRepositoryImpl,
    ): MessageApiRepository

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
     * Auth token manager — responsible for storing and retrieving the current
     */
    @Provides
    @Singleton
    fun provideAuthTokenManager(
        @UnencryptedPreferences sharedPreferences: SharedPreferences,
    ): AuthTokenManager = AuthTokenManagerImpl(
        sharedPreferences = sharedPreferences,
    )

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
