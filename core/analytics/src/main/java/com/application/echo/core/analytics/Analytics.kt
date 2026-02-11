package com.application.echo.core.analytics

/**
 * Analytics interface for tracking user interactions and app events.
 *
 * This interface provides methods to track various analytics events
 * and manage user properties for analytics purposes.
 */
interface Analytics {

    /**
     * Tracks an analytics event.
     *
     * @param event The analytics event to track
     */
    fun trackEvent(event: AnalyticsEvent)

    /**
     * Sets a user property for analytics.
     *
     * @param name The property name
     * @param value The property value
     */
    fun setUserProperty(name: String, value: String)

    /**
     * Sets the user ID for analytics.
     *
     * @param userId The user ID to set
     */
    fun setUserId(userId: String?)

    /**
     * Resets all user data and properties.
     * This should be called when user logs out.
     */
    fun resetUserData()

    /**
     * Enables or disables analytics collection.
     *
     * @param enabled Whether analytics collection should be enabled
     */
    fun setAnalyticsEnabled(enabled: Boolean)
}
