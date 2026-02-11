package com.application.echo.core.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of [Navigator].
 *
 * Uses a [Channel] for navigation commands to guarantee exactly-once delivery,
 * and a [MutableSharedFlow] for events to allow multiple observers.
 */
@Singleton
internal class NavigatorImpl @Inject constructor() : Navigator {

    private val commandChannel = Channel<NavigationCommand>(capacity = Channel.UNLIMITED)
    private val eventFlow = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 64)

    override val commands: Flow<NavigationCommand> = commandChannel.receiveAsFlow()

    override val events: Flow<NavigationEvent> = eventFlow.asSharedFlow()

    override fun navigateTo(route: Any) {
        commandChannel.trySend(NavigationCommand.NavigateTo(route))
        eventFlow.tryEmit(NavigationEvent.Navigated(route))
    }

    override fun navigateTo(route: Any, builder: NavigationOptionsBuilder.() -> Unit) {
        val options = NavigationOptionsBuilder().apply(builder).build()
        commandChannel.trySend(NavigationCommand.NavigateTo(route, options))
        eventFlow.tryEmit(NavigationEvent.Navigated(route))
    }

    override fun navigateToRoot(route: Any) {
        commandChannel.trySend(NavigationCommand.NavigateToRoot(route))
        eventFlow.tryEmit(NavigationEvent.Navigated(route))
    }

    override fun navigateBack() {
        commandChannel.trySend(NavigationCommand.NavigateBack)
        eventFlow.tryEmit(NavigationEvent.NavigatedBack)
    }

    override fun navigateBackTo(route: Any, inclusive: Boolean) {
        commandChannel.trySend(NavigationCommand.NavigateBackTo(route, inclusive))
        eventFlow.tryEmit(NavigationEvent.BackStackCleared(route, inclusive))
    }

    override fun <T : Any> navigateBackWithResult(key: String, value: T) {
        commandChannel.trySend(NavigationCommand.NavigateBackWithResult(key, value))
        eventFlow.tryEmit(NavigationEvent.NavigatedBack)
    }
}
