package com.application.echo.core.navigation.interceptor

import com.application.echo.core.navigation.NavigationCommand
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Runs all registered [NavigationInterceptor]s in [NavigationInterceptor.priority] order.
 *
 * If any interceptor returns `null`, the chain short-circuits and the navigation is cancelled.
 * If an interceptor returns a different command, subsequent interceptors see the modified command.
 */
@Singleton
internal class NavigationInterceptorChain @Inject constructor(
    interceptors: Set<@JvmSuppressWildcards NavigationInterceptor>,
) {

    private val sorted: List<NavigationInterceptor> = interceptors.sortedBy { it.priority }

    /**
     * Run all interceptors against [command].
     *
     * @return The final command to dispatch, or `null` if navigation was cancelled.
     */
    suspend fun process(command: NavigationCommand): NavigationCommand? {
        var current: NavigationCommand = command
        for (interceptor in sorted) {
            val result = interceptor.intercept(current)
            if (result == null) {
                Timber.tag(TAG).d("Navigation cancelled by %s", interceptor::class.simpleName)
                return null
            }
            if (result !== current) {
                Timber.tag(TAG).d(
                    "Navigation redirected by %s: %s → %s",
                    interceptor::class.simpleName,
                    current::class.simpleName,
                    result::class.simpleName,
                )
            }
            current = result
        }
        return current
    }

    companion object {
        private const val TAG = "EchoNav"
    }
}
