package com.application.echo.api.di

import android.content.SharedPreferences
import com.application.echo.api.auth.AuthApiRepository
import com.application.echo.api.auth.AuthApiService
import com.application.echo.api.auth.AuthApiRepositoryImpl
import com.application.echo.api.auth.EchoTokenRefreshListener
import com.application.echo.api.auth.TokenRefreshAuthenticator
import com.application.echo.api.auth.TokenRefreshListener
import com.application.echo.api.health.HealthRepository
import com.application.echo.api.health.HealthRepositoryImpl
import com.application.echo.api.manager.AuthTokenManager
import com.application.echo.api.manager.AuthTokenManagerImpl
import com.application.echo.api.media.MediaApiService
import com.application.echo.api.media.MediaApiRepository
import com.application.echo.api.media.MediaApiRepositoryImpl
import com.application.echo.api.message.MessageApiService
import com.application.echo.api.message.MessageApiRepository
import com.application.echo.api.message.MessageApiRepositoryImpl
import com.application.echo.api.session.SessionHeaderInterceptor
import com.application.echo.api.session.SessionProvider
import com.application.echo.api.profile.ProfileApiService
import com.application.echo.api.profile.ProfileApiRepository
import com.application.echo.api.profile.ProfileApiRepositoryImpl
import com.application.echo.core.common.annotations.UnencryptedPreferences
import com.application.echo.core.network.client.EchoHttpClient
import com.application.echo.core.network.interceptor.AuthTokenProvider
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
    abstract fun bindTokenRefreshListener(
        impl: EchoTokenRefreshListener,
    ): TokenRefreshListener

    @Binds
    @Singleton
    abstract fun bindAuthenticator(
        impl: TokenRefreshAuthenticator,
    ): okhttp3.Authenticator

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthApiRepositoryImpl,
    ): AuthApiRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: ProfileApiRepositoryImpl,
    ): ProfileApiRepository

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
     * access/refresh tokens from disk.
     */
    @Provides
    @Singleton
    fun provideAuthTokenManager(
        @UnencryptedPreferences sharedPreferences: SharedPreferences,
    ): AuthTokenManager = AuthTokenManagerImpl(
        sharedPreferences = sharedPreferences,
    )

    /**
     * Bridge so the network module's [AuthInterceptor] can read the current
     * Bearer token without depending on the API module directly.
     */
    @Provides
    @Singleton
    fun provideAuthTokenProvider(
        manager: AuthTokenManager,
    ): AuthTokenProvider = manager


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
    ): ProfileApiService = client.authenticated.create(ProfileApiService::class.java)

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
     * Add this interceptor to your OkHttpClient via [EchoHttpClient].
     */
    @Provides
    @Singleton
    @SessionInterceptor
    fun provideSessionHeaderInterceptor(
        sessionProvider: SessionProvider,
    ): Interceptor = SessionHeaderInterceptor(sessionProvider)
}
