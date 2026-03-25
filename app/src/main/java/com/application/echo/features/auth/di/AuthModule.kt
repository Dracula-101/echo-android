package com.application.echo.features.auth.di

import android.content.SharedPreferences
import com.application.echo.core.api.auth.AuthApiRepository
import com.application.echo.core.api.manager.AuthTokenManager
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.annotations.UnencryptedPreferences
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.features.auth.datasource.disk.AuthDiskSource
import com.application.echo.features.auth.datasource.disk.AuthDiskSourceImpl
import com.application.echo.features.auth.datasource.network.AuthNetworkSource
import com.application.echo.features.auth.datasource.network.AuthNetworkSourceImpl
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.auth.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthDiskSource(
        @UnencryptedPreferences sharedPreferences: SharedPreferences,
        json: Json,
    ): AuthDiskSource = AuthDiskSourceImpl(
        sharedPreferences = sharedPreferences,
        json = json,
    )

    @Provides
    @Singleton
    fun provideAuthNetworkSource(
        authApi: AuthApiRepository,
    ): AuthNetworkSource = AuthNetworkSourceImpl(
        api = authApi,
    )

    @Provides
    @Singleton
    fun provideAuthRepository(
        networkSource: AuthNetworkSource,
        diskSource: AuthDiskSource,
        tokenManager: AuthTokenManager,
        @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): AuthRepository = AuthRepositoryImpl(
        networkSource = networkSource,
        diskSource = diskSource,
        tokenManager = tokenManager,
        ioDispatcher = ioDispatcher,
    )
}
