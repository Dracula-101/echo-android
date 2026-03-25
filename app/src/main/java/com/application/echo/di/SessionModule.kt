package com.application.echo.di

import android.content.Context
import android.provider.Settings
import com.application.echo.core.api.session.DeviceInfo
import com.application.echo.core.api.session.SessionProvider
import com.application.echo.features.auth.datasource.disk.AuthDiskSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {

    @Provides
    @Singleton
    fun provideSessionProvider(
        @ApplicationContext context: Context,
        authDiskSource: AuthDiskSource,
    ): SessionProvider {
        val deviceInfo = DeviceInfo(
            deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID,
            ) ?: android.os.Build.FINGERPRINT,
            deviceName = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}",
        )
        return object : SessionProvider {
            override val sessionId: String?
                get() = authDiskSource.sessionId
            override val sessionToken: String?
                get() = authDiskSource.sessionToken
            override val deviceInfo: DeviceInfo
                get() = deviceInfo
        }
    }
}
