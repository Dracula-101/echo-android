package com.application.echo.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlin.reflect.KClass

/**
 * Registers a composable destination with optional transition overrides.
 *
 * This is a convenience wrapper over [NavGraphBuilder.composable] that applies the standard
 * forward/back navigation transitions used across the app. Feature modules can use this
 * to avoid duplicating transition configuration.
 *
 * @param enterTransition Custom enter transition override.
 * @param exitTransition Custom exit transition override.
 * @param popEnterTransition Custom pop enter transition override.
 * @param popExitTransition Custom pop exit transition override.
 * @param content The composable content for this destination.
 */
inline fun <reified T : Any> NavGraphBuilder.echoComposable(
    noinline enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    noinline exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    noinline popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    noinline popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    noinline content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable<T>(
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = { backStackEntry -> content(backStackEntry) },
    )
}

/**
 * Creates a nested navigation graph with type-safe routes.
 *
 * Feature modules use this to register their sub-graphs into the parent
 * [NavGraphBuilder]. Each feature exposes a `NavGraphBuilder.featureXxxGraph()`
 * extension that internally calls this helper.
 *
 * Example:
 * ```
 * fun NavGraphBuilder.authGraph(navigator: Navigator, onLoginSuccess: () -> Unit) {
 *     echoNavigation<AuthGraph>(startDestination = LoginRoute::class) {
 *         echoComposable<LoginRoute> { LoginScreen(...) }
 *         echoComposable<SignUpRoute> { SignUpScreen(...) }
 *     }
 * }
 * ```
 *
 * @param startDestination The [KClass] of the start destination inside this graph.
 * @param builder The [NavGraphBuilder] block to declare child destinations.
 */
inline fun <reified T : Any> NavGraphBuilder.echoNavigation(
    startDestination: KClass<*>,
    noinline builder: NavGraphBuilder.() -> Unit,
) {
    navigation<T>(
        startDestination = startDestination,
        builder = builder,
    )
}
