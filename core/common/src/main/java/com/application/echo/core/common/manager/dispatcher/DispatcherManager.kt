package com.application.echo.core.common.manager.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher

/**
 * Interface for managing coroutine dispatchers.
 * Provides access to various [CoroutineDispatcher]s for different threading contexts.
 */
interface DispatcherManager {

    /**
     * The default [CoroutineDispatcher] for general-purpose tasks.
     */
    val default: CoroutineDispatcher

    /**
     * The [MainCoroutineDispatcher] for UI-related tasks.
     */
    val main: MainCoroutineDispatcher

    /**
     * The IO [CoroutineDispatcher] for input/output operations.
     */
    val io: CoroutineDispatcher

    /**
     * The unconfined [CoroutineDispatcher] for tasks that are not bound to any specific thread.
     */
    val unconfined: CoroutineDispatcher
}