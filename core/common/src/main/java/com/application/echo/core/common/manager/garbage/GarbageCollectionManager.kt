package com.application.echo.core.common.manager.garbage

/**
 * Interface for managing garbage collection routines.
 * Provides methods to trigger garbage collection with delays and retries.
 */
interface GarbageCollectionManager {

    /**
     * Starts the garbage collection routine with delays and retries.
     */
    fun trigger()
}