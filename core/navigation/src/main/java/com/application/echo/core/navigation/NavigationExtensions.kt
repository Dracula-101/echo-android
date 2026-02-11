package com.application.echo.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.coroutines.flow.map

/**
 * Returns a lifecycle-aware [State] of the current [NavDestination].
 *
 * Recomposes only when the current destination actually changes, making it efficient
 * for bottom navigation bars and other UI that highlights the active destination.
 */
@Composable
fun NavController.currentDestinationAsState(): State<NavDestination?> =
    currentBackStackEntryFlow
        .map { it.destination }
        .collectAsStateWithLifecycle(initialValue = currentDestination)

/**
 * Returns a lifecycle-aware [State] of the current route string.
 */
@Composable
fun NavController.currentRouteAsState(): State<String?> =
    currentBackStackEntryFlow
        .map { it.destination.route }
        .collectAsStateWithLifecycle(initialValue = currentDestination?.route)

/**
 * Checks whether the given [route] type is currently on the back stack.
 */
inline fun <reified T : Any> NavController.isOnBackStack(): Boolean =
    currentBackStack.value.any { entry ->
        entry.destination.hasRoute<T>()
    }

/**
 * Retrieves a result value from the current back stack entry's saved state.
 *
 * This is the receiving side of [Navigator.navigateBackWithResult].
 *
 * @param key The key the result was stored under.
 * @return A lifecycle-aware [State] containing the result, or `null` if not yet available.
 */
@Composable
inline fun <reified T> NavBackStackEntry.rememberResult(key: String): State<T?> =
    savedStateHandle.getStateFlow<T?>(key, null)
        .collectAsStateWithLifecycle()
