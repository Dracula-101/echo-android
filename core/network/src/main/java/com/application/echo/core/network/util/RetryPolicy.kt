package com.application.echo.core.network.util

import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.model.NetworkResponse
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * Configurable retry policy for transient network errors.
 *
 * @property maxRetries Maximum number of retry attempts.
 * @property initialDelayMs Initial delay before the first retry.
 * @property backoffMultiplier Multiplier applied to the delay after each retry.
 * @property retryOnTimeout Whether to retry on [NetworkException.Timeout].
 * @property retryOnNetwork Whether to retry on [NetworkException.Network].
 * @property retryOnServerError Whether to retry on 5xx HTTP errors.
 */
data class RetryPolicy(
    val maxRetries: Int = 3,
    val initialDelayMs: Long = 1_000L,
    val backoffMultiplier: Double = 2.0,
    val retryOnTimeout: Boolean = true,
    val retryOnNetwork: Boolean = true,
    val retryOnServerError: Boolean = true,
) {
    companion object {
        /** No retries at all. */
        val NONE = RetryPolicy(maxRetries = 0)

        /** Default retry policy. */
        val DEFAULT = RetryPolicy()
    }
}

/**
 * Executes [block] with automatic retries according to [policy].
 *
 * ```
 * val response = withRetry(RetryPolicy.DEFAULT) {
 *     api.getUser()
 * }
 * ```
 */
suspend fun <T> withRetry(
    policy: RetryPolicy = RetryPolicy.DEFAULT,
    block: suspend () -> NetworkResponse<T>,
): NetworkResponse<T> {
    var lastResponse: NetworkResponse<T>? = null
    var currentDelay = policy.initialDelayMs

    repeat(policy.maxRetries + 1) { attempt ->
        val response = block()

        if (response is NetworkResponse.Success) return response

        lastResponse = response

        val error = (response as NetworkResponse.Error).error
        val shouldRetry = when {
            attempt >= policy.maxRetries -> false
            error is NetworkException.Timeout && policy.retryOnTimeout -> true
            error is NetworkException.Network && policy.retryOnNetwork -> true
            error is NetworkException.Http && error.isServerError && policy.retryOnServerError -> true
            else -> false
        }

        if (shouldRetry) {
            Timber.d("Retrying request (attempt ${attempt + 1}/${policy.maxRetries})…")
            delay(currentDelay)
            currentDelay = (currentDelay * policy.backoffMultiplier).toLong()
        } else {
            return response
        }
    }

    return lastResponse!!
}
