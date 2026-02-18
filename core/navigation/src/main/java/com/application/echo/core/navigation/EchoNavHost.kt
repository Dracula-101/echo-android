package com.application.echo.core.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.application.echo.core.navigation.transition.EchoTransitionPreset
import com.application.echo.core.navigation.transition.EchoTransitions
import com.application.echo.core.navigation.transition.RootEnterTransitionProvider
import com.application.echo.core.navigation.transition.RootExitTransitionProvider
import timber.log.Timber

/**
 * Echo wrapper around [NavHost] that wires up the [Navigator] automatically.
 *
 * It collects [NavigationCommand]s from the injected [Navigator] and translates them
 * into NavController calls. The [Navigator] is also exposed via [LocalNavigator] so
 * child composables can trigger navigation without a NavController reference.
 *
 * Uses [RootEnterTransitionProvider] / [RootExitTransitionProvider] (non-null) since
 * the host is the final fallback — it must always resolve to a concrete transition.
 * Individual composable destinations can use nullable providers that return `null`
 * to defer back to these host defaults.
 *
 * @param navigator The [Navigator] instance, typically injected via Hilt.
 * @param startDestination The route for the start destination.
 * @param modifier Optional [Modifier] applied to the NavHost.
 * @param enterTransition Default enter transition for all destinations.
 * @param exitTransition Default exit transition for all destinations.
 * @param popEnterTransition Default pop enter transition for all destinations.
 * @param popExitTransition Default pop exit transition for all destinations.
 * @param builder The [NavGraphBuilder] lambda used to declare destinations.
 */
@Composable
fun EchoNavHost(
    navigator: Navigator,
    startDestination: Any,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    enterTransition: RootEnterTransitionProvider = EchoTransitions.Enter.slideFromEnd,
    exitTransition: RootExitTransitionProvider = EchoTransitions.Exit.slideToStart,
    popEnterTransition: RootEnterTransitionProvider = EchoTransitions.Enter.slideFromStart,
    popExitTransition: RootExitTransitionProvider = EchoTransitions.Exit.slideToEnd,
    builder: NavGraphBuilder.() -> Unit,
) {
    NavigationCommandHandler(navController, navigator)

    CompositionLocalProvider(LocalNavigator provides navigator) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
            builder = builder,
            modifier = modifier.background(MaterialTheme.colorScheme.surface),
        )
    }
}

/**
 * Convenience overload that accepts an [EchoTransitionPreset] to set all four
 * transition directions in one shot.
 *
 * ```kotlin
 * EchoNavHost(
 *     navigator = navigator,
 *     startDestination = HomeRoute,
 *     transition = EchoTransitionPreset.Fade,
 * ) { ... }
 * ```
 *
 * @param transition The [EchoTransitionPreset] to apply as host defaults.
 */
@Composable
fun EchoNavHost(
    navigator: Navigator,
    startDestination: Any,
    transition: EchoTransitionPreset,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    builder: NavGraphBuilder.() -> Unit,
) {
    EchoNavHost(
        navigator = navigator,
        startDestination = startDestination,
        modifier = modifier,
        navController = navController,
        enterTransition = transition.enterTransition,
        exitTransition = transition.exitTransition,
        popEnterTransition = transition.popEnterTransition,
        popExitTransition = transition.popExitTransition,
        builder = builder,
    )
}

/**
 * Observes [NavigationCommand]s from the [Navigator] and dispatches them to the [NavHostController].
 */
@Composable
private fun NavigationCommandHandler(
    navController: NavHostController,
    navigator: Navigator,
) {
    LaunchedEffect(navController, navigator) {
        navigator.commands.collect { command ->
            when (command) {
                is NavigationCommand.NavigateTo -> {
                    val navOptions = command.navOptions
                    if (navOptions != null) {
                        navController.navigate(command.route, navOptions)
                    } else {
                        navController.navigate(command.route)
                    }
                }

                is NavigationCommand.NavigateToRoot -> {
                    navController.navigate(command.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                is NavigationCommand.NavigateBack -> {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        Timber.tag(TAG).d("Already at start destination, ignoring back")
                    }
                }

                is NavigationCommand.NavigateBackTo -> {
                    navController.popBackStack(
                        route = command.route,
                        inclusive = command.inclusive,
                    )
                }

                is NavigationCommand.NavigateBackWithResult<*> -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(command.key, command.value)
                    navController.popBackStack()
                }
            }
        }
    }
}

private const val TAG = "EchoNav"
