package com.application.echo.features.otp.di

import android.content.SharedPreferences
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.annotations.EncryptedPreferences
import com.application.echo.core.common.annotations.UnencryptedPreferences
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.features.auth.datasource.network.AuthNetworkSource
import com.application.echo.features.otp.datasource.disk.OtpDiskSource
import com.application.echo.features.otp.datasource.disk.OtpDiskSourceImpl
import com.application.echo.features.otp.repository.OtpRepository
import com.application.echo.features.otp.repository.OtpRepositoryImpl
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
object OtpModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideOtpDiskSource(
        @UnencryptedPreferences sharedPreferences: SharedPreferences,
        @EncryptedPreferences encryptedSharedPreferences: SharedPreferences,
        json: Json,
    ): OtpDiskSource = OtpDiskSourceImpl(
        sharedPreferences = sharedPreferences,
        encryptedSharedPreferences = encryptedSharedPreferences,
        json = json,
    )

    @Provides
    @Singleton
    fun provideOtpRepository(
        networkSource: AuthNetworkSource,
        otpDiskSource: OtpDiskSource,
        firebaseAuth: FirebaseAuth,
        @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): OtpRepository = OtpRepositoryImpl(
        networkSource = networkSource,
        otpDiskSource = otpDiskSource,
        firebaseAuth = firebaseAuth,
        ioDispatcher = ioDispatcher,
    )
}