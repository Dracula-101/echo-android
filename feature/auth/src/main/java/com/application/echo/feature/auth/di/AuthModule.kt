package com.application.echo.feature.auth.di

import android.content.SharedPreferences
import com.application.echo.core.api.auth.AuthApiRepository
import com.application.echo.core.common.annotations.UnencryptedPreferences
import com.application.echo.feature.auth.datasource.disk.AuthDiskSource
import com.application.echo.feature.auth.datasource.disk.AuthDiskSourceImpl
import com.application.echo.feature.auth.datasource.network.AuthNetworkSource
import com.application.echo.feature.auth.datasource.network.AuthNetworkSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthDiskSource(
        @UnencryptedPreferences sharedPreferences: SharedPreferences,
        json: Json
    ): AuthDiskSource {
        return AuthDiskSourceImpl(
            sharedPreferences = sharedPreferences,
            json = json
        )
    }

    @Provides
    @Singleton
    fun provideAuthNetworkSource(
        authApi: AuthApiRepository
    ): AuthNetworkSource {
        return AuthNetworkSourceImpl(
            api = authApi
        )
    }
}