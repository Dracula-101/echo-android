package com.application.echo.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import timber.log.Timber

/**
 * Echo-flavored wrapper around [NavHost] that wires up the [Navigator] automatically.
 *
 * It collects [NavigationCommand]s from the injected [Navigator] and translates them
 * into NavController calls. The [Navigator] is also exposed via [LocalNavigator] so
 * child composables can trigger navigation without a NavController reference.
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
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        { EnterTransition.None },
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        { ExitTransition.None },
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        enterTransition,
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        exitTransition,
    builder: NavGraphBuilder.() -> Unit,
) {
    NavigationCommandHandler(navController, navigator)

    CompositionLocalProvider(LocalNavigator provides navigator) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
            builder = builder,
        )
    }
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
                    navController.navigate(command.route) {
                        command.navOptions?.let { options ->
                            launchSingleTop = options.shouldLaunchSingleTop()
                            restoreState = options.shouldRestoreState()
                        }
                    }
                }

                is NavigationCommand.NavigateToRoot -> {
                    navController.navigate(command.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                is NavigationCommand.NavigateBack -> {
                    if (!navController.popBackStack()) {
                        Timber.d("Navigation: back stack is empty, cannot pop")
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
