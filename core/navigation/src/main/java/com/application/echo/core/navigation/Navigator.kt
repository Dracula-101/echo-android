package com.application.echo.core.navigation

import kotlinx.coroutines.flow.Flow

/**
 * Contract for a centralized navigation coordinator.
 *
 * [Navigator] decouples navigation actions from the NavController lifecycle by buffering
 * [NavigationCommand]s in a [Flow] that the NavHost observes. This allows ViewModels and
 * other non-UI components to trigger navigation without holding a reference to a
 * NavController.
 *
 * Navigation events are also emitted for observability (analytics, logging).
 */
interface Navigator {

    /**
     * A [Flow] of [NavigationCommand]s to be consumed by the NavHost.
     * Each command is processed exactly once.
     */
    val commands: Flow<NavigationCommand>

    /**
     * A [Flow] of [NavigationEvent]s representing navigation lifecycle changes.
     * Suitable for analytics tracking and logging.
     */
    val events: Flow<NavigationEvent>

    /**
     * Navigate to a destination identified by [route].
     */
    fun navigateTo(route: Any)

    /**
     * Navigate to a destination with custom [NavOptions][androidx.navigation.NavOptions].
     */
    fun navigateTo(route: Any, builder: NavigationOptionsBuilder.() -> Unit)

    /**
     * Navigate to a top-level destination, clearing the back stack above it.
     */
    fun navigateToRoot(route: Any)

    /**
     * Navigate back to the previous destination.
     */
    fun navigateBack()

    /**
     * Navigate back to a specific [route] on the back stack.
     *
     * @param inclusive Whether [route] itself should also be popped.
     */
    fun navigateBackTo(route: Any, inclusive: Boolean = false)

    /**
     * Navigate back with a result attached to the previous back stack entry.
     *
     * @param key The key under which the result is stored.
     * @param value The result value.
     */
    fun <T : Any> navigateBackWithResult(key: String, value: T)
}
