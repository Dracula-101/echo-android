package com.application.echo.di

import android.content.Context
import android.content.SharedPreferences
import com.application.echo.core.common.annotations.UnencryptedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @UnencryptedPreferences
    fun provideUnencryptedSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences = context.getSharedPreferences(
        "echo_prefs",
        Context.MODE_PRIVATE,
    )
}
