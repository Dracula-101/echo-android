package com.application.echo.core.navigation

import androidx.navigation.NavOptions

/**
 * Represents a navigation action that can be dispatched through the [Navigator].
 *
 * Each variant encapsulates the data needed to perform a specific type of navigation,
 * keeping navigation logic decoupled from UI components and ViewModels.
 */
sealed interface NavigationCommand {

    /**
     * Navigate to a destination identified by [route].
     *
     * @property route The destination route to navigate to.
     * @property navOptions Optional [NavOptions] to control the navigation behavior
     *   such as pop-up-to, single top, and animations.
     */
    data class NavigateTo(
        val route: Any,
        val navOptions: NavOptions? = null,
    ) : NavigationCommand

    /**
     * Navigate to a destination and clear the entire back stack up to the given [route].
     * Useful for top-level destination switches such as post-login or bottom navigation tabs.
     *
     * @property route The destination route to navigate to.
     */
    data class NavigateToRoot(
        val route: Any,
    ) : NavigationCommand

    /**
     * Navigate back to the previous destination on the back stack.
     */
    data object NavigateBack : NavigationCommand

    /**
     * Navigate back to a specific [route] on the back stack, popping all destinations above it.
     *
     * @property route The destination route to pop back to.
     * @property inclusive Whether the destination itself should also be popped.
     */
    data class NavigateBackTo(
        val route: Any,
        val inclusive: Boolean = false,
    ) : NavigationCommand

    /**
     * Navigate back with a result value attached to the previous back stack entry's saved state.
     *
     * @property key The key under which the result is stored.
     * @property value The result value. Must be a type that can be stored in a [android.os.Bundle].
     */
    data class NavigateBackWithResult<T : Any>(
        val key: String,
        val value: T,
    ) : NavigationCommand
}
