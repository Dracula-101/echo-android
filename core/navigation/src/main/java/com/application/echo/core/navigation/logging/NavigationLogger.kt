package com.application.echo.core.navigation.logging

import com.application.echo.core.navigation.NavigationCommand
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Structured Timber logger for all navigation events.
 *
 * Produces output like:
 * ```
 * D/EchoNav: → NavigateTo  route=HomeRoute
 * D/EchoNav: → NavigateToRoot  route=MainGraph
 * D/EchoNav: ← Back
 * D/EchoNav: ← BackTo  route=LoginRoute  inclusive=true
 * D/EchoNav: ← BackWithResult  key=result_ok
 * ```
 */
@Singleton
internal class NavigationLogger @Inject constructor() {

    fun log(command: NavigationCommand) {
        val message = when (command) {
            is NavigationCommand.NavigateTo -> {
                val opts = if (command.navOptions != null) "  options=✓" else ""
                "→ NavigateTo  route=${routeName(command.route)}$opts"
            }

            is NavigationCommand.NavigateToRoot -> {
                "→ NavigateToRoot  route=${routeName(command.route)}"
            }

            is NavigationCommand.NavigateBack -> {
                "← Back"
            }

            is NavigationCommand.NavigateBackTo -> {
                "← BackTo  route=${routeName(command.route)}  inclusive=${command.inclusive}"
            }

            is NavigationCommand.NavigateBackWithResult<*> -> {
                "← BackWithResult  key=${command.key}"
            }
        }
        Timber.tag(TAG).d(message)
    }

    fun logCancelled(command: NavigationCommand) {
        Timber.tag(TAG).d("✕ Cancelled  %s", routeName(command))
    }

    private fun routeName(route: Any): String {
        val name = route::class.simpleName ?: route.toString()
        // For data classes with params, include the toString
        return if (route::class.isData && name != route.toString()) {
            route.toString()
        } else {
            name
        }
    }

    companion object {
        internal const val TAG = "EchoNav"
    }
}
