package com.application.echo.core.common.manager.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

/**
 * Primary implementation of [DispatcherManager].
 */
class DispatcherManagerImpl : DispatcherManager {
    // Default dispatcher for general-purpose tasks.
    override val default: CoroutineDispatcher = Dispatchers.Default

    // Main dispatcher for UI-related tasks.
    override val main: MainCoroutineDispatcher = Dispatchers.Main

    // IO dispatcher for input/output operations.
    override val io: CoroutineDispatcher = Dispatchers.IO

    // Unconfined dispatcher for tasks not bound to any specific thread.
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
