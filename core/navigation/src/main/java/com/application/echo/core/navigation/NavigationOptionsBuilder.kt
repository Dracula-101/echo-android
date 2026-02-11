package com.application.echo.core.navigation

import androidx.navigation.NavOptions
import androidx.navigation.navOptions

/**
 * DSL builder for constructing [NavOptions] in a readable, Kotlin-idiomatic way.
 *
 * Used by [Navigator.navigateTo] to provide a clean API:
 * ```
 * navigator.navigateTo(HomeRoute) {
 *     popUpTo(LoginRoute, inclusive = true)
 *     launchSingleTop = true
 * }
 * ```
 */
class NavigationOptionsBuilder {
    /**
     * Whether this navigation action should launch as single-top,
     * avoiding multiple copies of the same destination on the back stack.
     */
    var launchSingleTop: Boolean = false

    /**
     * Whether the back stack should be restored when navigating to a
     * previously visited destination.
     */
    var restoreState: Boolean = false

    private var popUpToRoute: Any? = null
    private var popUpToInclusive: Boolean = false
    private var popUpToSaveState: Boolean = false

    /**
     * Pop destinations off the back stack up to [route].
     *
     * @param route The destination to pop up to.
     * @param inclusive Whether [route] itself should be popped.
     * @param saveState Whether the state of popped destinations should be saved.
     */
    fun popUpTo(route: Any, inclusive: Boolean = false, saveState: Boolean = false) {
        popUpToRoute = route
        popUpToInclusive = inclusive
        popUpToSaveState = saveState
    }

    internal fun build(): NavOptions = navOptions {
        this@NavigationOptionsBuilder.popUpToRoute?.let { route ->
            popUpTo(route.hashCode()) {
                inclusive = this@NavigationOptionsBuilder.popUpToInclusive
                saveState = this@NavigationOptionsBuilder.popUpToSaveState
            }
        }
        launchSingleTop = this@NavigationOptionsBuilder.launchSingleTop
        restoreState = this@NavigationOptionsBuilder.restoreState
    }
}
