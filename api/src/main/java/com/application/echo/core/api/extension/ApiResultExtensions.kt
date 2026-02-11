package com.application.echo.core.api.extension

import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.result.ApiResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Flow wrappers
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Wraps a suspend API call into a [Flow] that emits the [ApiResult].
 *
 * ```kotlin
 * apiFlow { authRepo.login(email, password) }
 *     .onEach { result -> updateState(result) }
 *     .launchIn(viewModelScope)
 * ```
 */
fun <T> apiFlow(
    call: suspend () -> ApiResult<T>,
): Flow<ApiResult<T>> = flow {
    emit(call())
}

/**
 * Wraps a suspend API call into a [Flow] that emits [UiState] events:
 * Loading → Success/Failure.
 *
 * ```kotlin
 * apiFlowWithLoading { userRepo.getProfile(userId) }
 *     .collect { state ->
 *         when (state) {
 *             is UiState.Loading -> showLoader()
 *             is UiState.Success -> showProfile(state.data)
 *             is UiState.Failure -> showError(state.exception)
 *         }
 *     }
 * ```
 */
fun <T> apiFlowWithLoading(
    call: suspend () -> ApiResult<T>,
): Flow<UiState<T>> = flow<UiState<T>> {
    val result = call()
    when (result) {
        is ApiResult.Success -> emit(UiState.Success(result.data))
        is ApiResult.Failure -> emit(UiState.Failure(result.exception))
    }
}.onStart {
    emit(UiState.Loading)
}.catch { throwable ->
    Timber.e(throwable, "Unhandled exception in apiFlowWithLoading")
    emit(UiState.Failure(NetworkException.Unknown(throwable)))
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  UiState
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Tri-state wrapper for driving UI from an API call.
 */
sealed interface UiState<out T> {

    data object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>

    data class Failure(val exception: NetworkException) : UiState<Nothing>
}

/**
 * Maps the success data inside a [UiState].
 */
inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Loading -> UiState.Loading
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Failure -> this
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Retry
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Retries a failing API call with exponential back-off.
 *
 * Only retries on transient errors (network, timeout, 5xx).
 * Client errors (4xx) are returned immediately.
 *
 * ```kotlin
 * val result = retryApiCall(maxRetries = 3) {
 *     messageRepo.getMyConversations()
 * }
 * ```
 */
suspend fun <T> retryApiCall(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1_000L,
    backoffMultiplier: Double = 2.0,
    call: suspend () -> ApiResult<T>,
): ApiResult<T> {
    var lastResult: ApiResult<T> = call()
    var currentDelay = initialDelayMs

    repeat(maxRetries) { attempt ->
        if (lastResult is ApiResult.Success) return lastResult

        val exception = (lastResult as ApiResult.Failure).exception
        if (!exception.isRetryable()) return lastResult

        Timber.d("Retrying API call (attempt ${attempt + 1}/$maxRetries)")
        delay(currentDelay)
        currentDelay = (currentDelay * backoffMultiplier).toLong()

        lastResult = call()
    }

    return lastResult
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Flow<ApiResult> operators
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Maps the success data inside a [Flow] of [ApiResult].
 */
fun <T, R> Flow<ApiResult<T>>.mapSuccess(
    transform: (T) -> R,
): Flow<ApiResult<R>> = map { result ->
    when (result) {
        is ApiResult.Success -> ApiResult.Success(transform(result.data))
        is ApiResult.Failure -> result
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Helpers
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Whether this exception represents a transient failure worth retrying.
 */
fun NetworkException.isRetryable(): Boolean = when (this) {
    is NetworkException.Network -> true
    is NetworkException.Timeout -> true
    is NetworkException.Http -> isServerError || isRateLimited
    is NetworkException.Serialization -> false
    is NetworkException.Unknown -> false
}
