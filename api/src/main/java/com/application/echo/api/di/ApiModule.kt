package com.application.echo.api.di

import android.content.Context
import android.content.SharedPreferences
import com.application.echo.api.auth.AuthApiRepository
import com.application.echo.api.auth.AuthApiRepositoryImpl
import com.application.echo.api.auth.AuthApiService
import com.application.echo.api.auth.EchoTokenRefreshListener
import com.application.echo.api.auth.TokenRefreshAuthenticator
import com.application.echo.api.auth.TokenRefreshListener
import com.application.echo.api.health.HealthRepository
import com.application.echo.api.health.HealthRepositoryImpl
import com.application.echo.api.manager.AuthTokenManager
import com.application.echo.api.manager.AuthTokenManagerImpl
import com.application.echo.api.manager.SessionManager
import com.application.echo.api.manager.SessionManagerImpl
import com.application.echo.api.media.MediaApiRepository
import com.application.echo.api.media.MediaApiRepositoryImpl
import com.application.echo.api.media.MediaApiService
import com.application.echo.api.message.MessageApiRepository
import com.application.echo.api.message.MessageApiRepositoryImpl
import com.application.echo.api.message.MessageApiService
import com.application.echo.api.profile.ProfileApiRepository
import com.application.echo.api.profile.ProfileApiRepositoryImpl
import com.application.echo.api.profile.ProfileApiService
import com.application.echo.core.common.annotations.UnencryptedPreferences
import com.application.echo.core.network.client.EchoHttpClient
import com.application.echo.core.network.provider.AuthTokenProvider
import com.application.echo.core.network.provider.DeviceInfoProvider
import com.application.echo.core.network.provider.SessionProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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

	@Binds
	@Singleton
	abstract fun bindSessionManager(
		impl: SessionManagerImpl,
	): SessionManager
}

@Module
@InstallIn(SingletonComponent::class)
internal object ApiProvidesModule {

	@Provides
	@Singleton
	fun provideAuthTokenManager(
		@UnencryptedPreferences sharedPreferences: SharedPreferences,
	): AuthTokenManager = AuthTokenManagerImpl(
		sharedPreferences = sharedPreferences,
	)

	@Provides
	@Singleton
	fun provideAuthTokenProvider(
		manager: AuthTokenManager,
	): AuthTokenProvider = manager

	@Provides
	@Singleton
	fun provideDeviceInfoProvider(
		@ApplicationContext context: Context,
	): DeviceInfoProvider = DeviceInfoProvider(context)

	@Provides
	@Singleton
	fun provideSessionProvider(
		manager: SessionManager,
	): SessionProvider = manager

	@Provides
	@Singleton
	fun provideAuthApiService(
		client: EchoHttpClient,
	): AuthApiService = client.unauthenticated.create(AuthApiService::class.java)

	@Provides
	@Singleton
	fun provideUserApiService(
		client: EchoHttpClient,
	): ProfileApiService = client.authenticated.create(ProfileApiService::class.java)

	@Provides
	@Singleton
	fun provideMediaApiService(
		client: EchoHttpClient,
	): MediaApiService = client.authenticated.create(MediaApiService::class.java)

	@Provides
	@Singleton
	fun provideMessageApiService(
		client: EchoHttpClient,
	): MessageApiService = client.authenticated.create(MessageApiService::class.java)
}
