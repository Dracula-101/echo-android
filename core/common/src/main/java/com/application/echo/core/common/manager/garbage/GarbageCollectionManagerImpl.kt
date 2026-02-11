package com.application.echo.core.common.manager.garbage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Default handler that schedules delayed GC passes using coroutines.
 */
// Implementation of GarbageCollectionManager scheduling delayed GC passes using coroutines.
class GarbageCollectionManagerImpl(
    private val runGc: () -> Unit = { Runtime.getRuntime().gc() },
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) : GarbageCollectionManager {

    private var activeJob: Job = Job().apply { complete() }

    // Triggers garbage collection with initial delay and retry logic.
    override fun trigger() {
        activeJob.cancel()
        activeJob = coroutineScope.launch {
            delay(INITIAL_WAIT_MS)
            repeat(RETRY_LIMIT) { attempt ->
                delay(BACKOFF_MS * attempt)
                runGc()
            }
        }
    }
}

// Constants defining retry limits and delays for garbage collection.
/** How many times GC will be manually invoked. */
private const val RETRY_LIMIT = 10

/** Base interval (in ms) used to space out GC attempts. */
private const val BACKOFF_MS = 100L

/** Delay (in ms) before the first GC attempt kicks off. */
private const val INITIAL_WAIT_MS = 100L