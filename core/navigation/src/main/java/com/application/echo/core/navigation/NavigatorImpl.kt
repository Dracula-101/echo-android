package com.application.echo.core.navigation

import com.application.echo.core.navigation.interceptor.NavigationInterceptorChain
import com.application.echo.core.navigation.logging.NavigationLogger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of [Navigator].
 *
 * Uses a [Channel] for navigation commands to guarantee exactly-once delivery,
 * and a [MutableSharedFlow] for events to allow multiple observers.
 *
 * All commands pass through the [NavigationInterceptorChain] before being dispatched.
 * Every command is logged via [NavigationLogger].
 */
@Singleton
internal class NavigatorImpl @Inject constructor(
    private val interceptorChain: NavigationInterceptorChain,
    private val logger: NavigationLogger,
) : Navigator {

    private val commandChannel = Channel<NavigationCommand>(capacity = Channel.UNLIMITED)
    private val eventFlow = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 64)

    override val commands: Flow<NavigationCommand> = commandChannel.receiveAsFlow()

    override val events: Flow<NavigationEvent> = eventFlow.asSharedFlow()

    override fun navigateTo(route: Any) {
        dispatch(NavigationCommand.NavigateTo(route))
    }

    override fun navigateTo(route: Any, builder: NavigationOptionsBuilder.() -> Unit) {
        val options = NavigationOptionsBuilder().apply(builder).build()
        dispatch(NavigationCommand.NavigateTo(route, options))
    }

    override fun navigateToRoot(route: Any) {
        dispatch(NavigationCommand.NavigateToRoot(route))
    }

    override fun navigateBack() {
        dispatch(NavigationCommand.NavigateBack)
    }

    override fun navigateBackTo(route: Any, inclusive: Boolean) {
        dispatch(NavigationCommand.NavigateBackTo(route, inclusive))
    }

    override fun <T : Any> navigateBackWithResult(key: String, value: T) {
        dispatch(NavigationCommand.NavigateBackWithResult(key, value))
    }

    private fun dispatch(command: NavigationCommand) {
        val processed = runBlocking { interceptorChain.process(command) }
        if (processed == null) {
            logger.logCancelled(command)
            return
        }
        logger.log(processed)
        commandChannel.trySend(processed)
        eventFlow.tryEmit(processed.toEvent())
    }

    private fun NavigationCommand.toEvent(): NavigationEvent = when (this) {
        is NavigationCommand.NavigateTo -> NavigationEvent.Navigated(route)
        is NavigationCommand.NavigateToRoot -> NavigationEvent.Navigated(route)
        is NavigationCommand.NavigateBack -> NavigationEvent.NavigatedBack
        is NavigationCommand.NavigateBackTo -> NavigationEvent.BackStackCleared(route, inclusive)
        is NavigationCommand.NavigateBackWithResult<*> -> NavigationEvent.NavigatedBack
    }
}
