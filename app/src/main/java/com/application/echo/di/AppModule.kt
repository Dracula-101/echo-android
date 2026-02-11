package com.application.echo.di

import android.content.Context
import com.application.echo.core.common.manager.battery.BatteryCoreManager
import com.application.echo.core.common.manager.battery.BatteryCoreManagerImpl
import com.application.echo.core.common.manager.dispatcher.DispatcherManager
import com.application.echo.core.common.manager.dispatcher.DispatcherManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

//module
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideDispatcherManager(): DispatcherManager {
        return DispatcherManagerImpl()
    }

    @Provides
    fun provideBatteryManager(
        @ApplicationContext context: Context,
        dispatcherManager: DispatcherManager
    ): BatteryCoreManager {
        return BatteryCoreManagerImpl(
            context = context,
            dispatcherManager = dispatcherManager
        )
    }

}