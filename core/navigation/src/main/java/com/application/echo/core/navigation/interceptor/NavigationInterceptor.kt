package com.application.echo.core.navigation.interceptor

import com.application.echo.core.navigation.NavigationCommand

/**
 * Intercepts navigation commands before they are dispatched to the NavController.
 *
 * Interceptors run in [priority] order (lowest first). Each interceptor receives the
 * current command and may:
 * - **Pass through** — return the command unchanged.
 * - **Redirect** — return a different command.
 * - **Cancel** — return `null` to stop the navigation entirely.
 *
 * Common use-cases:
 * - Authentication guards (redirect unauthenticated users to login).
 * - Feature-flag gates (redirect to "coming soon" screen).
 * - Analytics enrichment.
 *
 * ```kotlin
 * class AuthGuardInterceptor @Inject constructor(
 *     private val authState: AuthStateProvider,
 * ) : NavigationInterceptor {
 *
 *     override val priority: Int = 0
 *
 *     override suspend fun intercept(command: NavigationCommand): NavigationCommand? {
 *         if (command is NavigationCommand.NavigateTo && requiresAuth(command.route)) {
 *             return if (authState.isLoggedIn) command
 *                    else NavigationCommand.NavigateTo(LoginRoute)
 *         }
 *         return command
 *     }
 * }
 * ```
 */
interface NavigationInterceptor {

    /**
     * Intercept a navigation [command].
     *
     * @return The command to dispatch (may be the original or a modified one),
     *   or `null` to cancel navigation.
     */
    suspend fun intercept(command: NavigationCommand): NavigationCommand?

    /**
     * Priority of this interceptor. Lower values run first.
     * Default is `0`.
     */
    val priority: Int get() = 0
}
