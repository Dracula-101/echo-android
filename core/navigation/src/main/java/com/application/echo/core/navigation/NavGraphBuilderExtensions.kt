package com.application.echo.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.application.echo.core.navigation.transition.EchoTransitionPreset
import kotlin.reflect.KClass

/**
 * Registers a composable destination with optional transition overrides and deep links.
 *
 * This is a convenience wrapper over [NavGraphBuilder.composable] that applies the standard
 * forward/back navigation transitions used across the app. Feature modules can use this
 * to avoid duplicating transition configuration.
 *
 * @param enterTransition Custom enter transition override.
 * @param exitTransition Custom exit transition override.
 * @param popEnterTransition Custom pop enter transition override.
 * @param popExitTransition Custom pop exit transition override.
 * @param deepLinks List of [NavDeepLink]s for this destination.
 * @param content The composable content for this destination.
 */
inline fun <reified T : Any> NavGraphBuilder.echoComposable(
    noinline enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    noinline exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    noinline popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    noinline popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable<T>(
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        deepLinks = deepLinks,
        content = { backStackEntry -> content(backStackEntry) },
    )
}

/**
 * Registers a composable destination with a bundled [EchoTransitionPreset].
 *
 * ```kotlin
 * echoComposable<SettingsRoute>(
 *     transition = EchoTransitionPreset.SlideHorizontal,
 * ) { SettingsScreen() }
 *
 * echoComposable<ProfileRoute>(
 *     transition = EchoTransitionPreset.Modal,
 *     deepLinks = echoDeepLinks { uriPattern("echo://profile/{userId}") },
 * ) { ProfileScreen() }
 * ```
 */
inline fun <reified T : Any> NavGraphBuilder.echoComposable(
    transition: EchoTransitionPreset,
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable (NavBackStackEntry) -> Unit,
) {
    echoComposable<T>(
        enterTransition = transition.enterTransition,
        exitTransition = transition.exitTransition,
        popEnterTransition = transition.popEnterTransition,
        popExitTransition = transition.popExitTransition,
        deepLinks = deepLinks,
        content = content,
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
