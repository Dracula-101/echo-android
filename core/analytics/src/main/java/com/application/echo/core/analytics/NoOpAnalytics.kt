package com.application.echo.core.analytics

import javax.inject.Inject
import javax.inject.Singleton

/**
 * No-op implementation of Analytics interface for development builds.
 *
 * This implementation does nothing and is used to avoid sending
 * analytics events during development and testing.
 */
@Singleton
class NoOpAnalytics @Inject constructor() : Analytics {

    override fun trackEvent(event: AnalyticsEvent) = Unit

    override fun setUserProperty(name: String, value: String) = Unit

    override fun setUserId(userId: String?) = Unit

    override fun resetUserData() = Unit

    override fun setAnalyticsEnabled(enabled: Boolean) = Unit
}
