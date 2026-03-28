package com.application.echo.features.profile.di

import android.content.SharedPreferences
import com.application.echo.core.api.auth.AuthApiRepository
import com.application.echo.core.api.manager.AuthTokenManager
import com.application.echo.core.api.profile.ProfileApiRepository
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.annotations.UnencryptedPreferences
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.features.auth.datasource.disk.AuthDiskSource
import com.application.echo.features.auth.datasource.disk.AuthDiskSourceImpl
import com.application.echo.features.auth.datasource.network.AuthNetworkSource
import com.application.echo.features.auth.datasource.network.AuthNetworkSourceImpl
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.auth.repository.AuthRepositoryImpl
import com.application.echo.features.profile.datasource.disk.ProfileDiskSource
import com.application.echo.features.profile.datasource.disk.ProfileDiskSourceImpl
import com.application.echo.features.profile.datasource.network.ProfileNetworkSource
import com.application.echo.features.profile.datasource.network.ProfileNetworkSourceImpl
import com.application.echo.features.profile.repository.ProfileRepository
import com.application.echo.features.profile.repository.ProfileRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileDiskSource(
        @UnencryptedPreferences sharedPreferences: SharedPreferences,
        json: Json,
    ): ProfileDiskSource = ProfileDiskSourceImpl(
        sharedPreferences = sharedPreferences,
        json = json,
    )

    @Provides
    @Singleton
    fun provideProfileNetworkSource(
        profileApi: ProfileApiRepository,
    ): ProfileNetworkSource = ProfileNetworkSourceImpl(
        profileApi = profileApi,
    )


    @Provides
    @Singleton
    fun provideProfileRepository(
        networkSource: ProfileNetworkSource,
        diskSource: ProfileDiskSource,
        authRepository: AuthRepository,
        @AppDispatcher(AppDispatchers.Default) defaultDispatcher: CoroutineDispatcher,
    ): ProfileRepository = ProfileRepositoryImpl(
        diskSource = diskSource,
        networkSource = networkSource,
        authRepository = authRepository,
        defaultDispatcher = defaultDispatcher,
    )
}
