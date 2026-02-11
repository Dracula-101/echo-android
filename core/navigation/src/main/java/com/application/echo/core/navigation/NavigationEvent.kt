package com.application.echo.core.navigation

/**
 * Represents observable navigation lifecycle events emitted by the [Navigator].
 *
 * These can be consumed by the UI layer for side effects such as analytics tracking,
 * logging, or coordinating animations that depend on navigation state changes.
 */
sealed interface NavigationEvent {

    /**
     * A forward navigation was performed to [route].
     */
    data class Navigated(val route: Any) : NavigationEvent

    /**
     * The user navigated back.
     */
    data object NavigatedBack : NavigationEvent

    /**
     * The back stack was cleared up to [route].
     *
     * @property inclusive Whether [route] itself was also popped.
     */
    data class BackStackCleared(
        val route: Any,
        val inclusive: Boolean,
    ) : NavigationEvent
}
