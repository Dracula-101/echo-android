package com.application.echo.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.coroutines.flow.map
import timber.log.Timber

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

// ──────────────── Debug Extensions ────────────────

/**
 * Null-safe access to the current route string.
 * Returns `null` instead of crashing when the back stack is empty.
 */
val NavController.currentRouteOrNull: String?
    get() = currentBackStackEntry?.destination?.route

/**
 * Number of entries on the back stack (including the current destination).
 */
val NavController.backStackDepth: Int
    get() = currentBackStack.value.size

/**
 * Logs the full back stack to Timber for debugging.
 *
 * Output:
 * ```
 * D/EchoNav: ── Back Stack (3 entries) ──
 * D/EchoNav:  [0] com.example/homeRoute
 * D/EchoNav:  [1] com.example/profileRoute
 * D/EchoNav:  [2] com.example/settingsRoute  ← current
 * D/EchoNav: ────────────────────────────────
 * ```
 */
fun NavController.printBackStack() {
    val entries = currentBackStack.value
    val lines = buildString {
        appendLine("── Back Stack (${entries.size} entries) ──")
        entries.forEachIndexed { index, entry ->
            val route = entry.destination.route ?: "?"
            val marker = if (index == entries.lastIndex) "  ← current" else ""
            appendLine(" [$index] $route$marker")
        }
        append("────────────────────────────────")
    }
    Timber.tag(TAG).d(lines)
}

private const val TAG = "EchoNav"
