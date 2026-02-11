package com.application.echo.core.analytics.di

import com.application.echo.core.analytics.Analytics
import com.application.echo.core.analytics.NoOpAnalytics
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Development-specific Hilt module for providing Analytics dependencies.
 *
 * This module binds the Analytics interface to NoOpAnalytics implementation
 * for development builds, preventing analytics events from being sent.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalyticsModule {

    /**
     * Binds the Analytics interface to NoOpAnalytics implementation.
     * This binding is used for development builds.
     */
    @Binds
    abstract fun bindAnalytics(noOpAnalytics: NoOpAnalytics): Analytics


}
