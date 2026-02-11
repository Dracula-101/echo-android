package com.application.echo.core.analytics.di

import com.application.echo.core.analytics.Analytics
import com.application.echo.core.analytics.FirebaseAnalyticsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Production-specific Hilt module for providing Analytics dependencies.
 *
 * This module binds the Analytics interface to FirebaseAnalyticsImpl
 * for production builds, enabling Firebase Analytics tracking.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalyticsModule {

    /**
     * Binds the Analytics interface to FirebaseAnalyticsImpl implementation.
     * This binding is used for production builds.
     */
    @Binds
    abstract fun bindAnalytics(firebaseAnalyticsImpl: FirebaseAnalyticsImpl): Analytics
}
