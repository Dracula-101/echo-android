package com.application.echo.features.auth.di

import android.content.SharedPreferences
import com.application.echo.api.auth.AuthApiRepository
import com.application.echo.api.manager.AuthTokenManager
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
import com.google.firebase.auth.FirebaseAuth
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
        @UnencryptedPreferences encryptedSharedPreferences: SharedPreferences,
        json: Json,
    ): AuthDiskSource = AuthDiskSourceImpl(
        sharedPreferences = sharedPreferences,
        encryptedSharedPreferences = encryptedSharedPreferences,
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
        authDiskSource: AuthDiskSource,
        profileDiskSource: ProfileDiskSource,
        tokenManager: AuthTokenManager,
        @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        firebaseAuth: FirebaseAuth,
    ): AuthRepository = AuthRepositoryImpl(
        networkSource = networkSource,
        authDiskSource = authDiskSource,
        profileDiskSource = profileDiskSource,
        tokenManager = tokenManager,
        ioDispatcher = ioDispatcher,
        firebaseAuth = firebaseAuth,
    )

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}
