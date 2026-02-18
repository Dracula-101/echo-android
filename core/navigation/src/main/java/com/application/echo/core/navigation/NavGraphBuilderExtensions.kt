package com.application.echo.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.application.echo.core.navigation.transition.EchoTransitionPreset
import com.application.echo.core.navigation.transition.EnterTransitionProvider
import com.application.echo.core.navigation.transition.ExitTransitionProvider
import kotlin.reflect.KClass

/**
 * Registers a composable destination with optional transition overrides and deep links.
 *
 * Uses nullable [EnterTransitionProvider] / [ExitTransitionProvider] — returning `null`
 * defers to the parent [EchoNavHost]'s default transition. This means individual
 * screens only need to specify transitions they want to override; everything else
 * falls through to the host.
 *
 * For graph-aware transitions that automatically return `null` on cross-graph
 * navigation, use providers from [EchoTransitionProviders.Enter] / [Exit].
 *
 * @param enterTransition Custom enter transition override (null = use host default).
 * @param exitTransition Custom exit transition override (null = use host default).
 * @param popEnterTransition Custom pop enter transition override (null = use host default).
 * @param popExitTransition Custom pop exit transition override (null = use host default).
 * @param deepLinks List of [NavDeepLink]s for this destination.
 * @param content The composable content for this destination.
 */
inline fun <reified T : Any> NavGraphBuilder.echoComposable(
    noinline enterTransition: EnterTransitionProvider = null,
    noinline exitTransition: ExitTransitionProvider = null,
    noinline popEnterTransition: EnterTransitionProvider = enterTransition,
    noinline popExitTransition: ExitTransitionProvider = exitTransition,
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
 * The preset's root-level providers are used directly — they always resolve
 * to a concrete transition for this screen.
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
