package com.application.echo.core.network.result

import com.application.echo.core.network.model.NetworkResponse

/**
 * Converts a [NetworkResponse] into a simpler [ApiResult].
 *
 * ```
 * val result: ApiResult<User> = api.getUser().toApiResult()
 * result.onSuccess { user -> ... }
 *       .onFailure { error -> ... }
 * ```
 */
fun <T> NetworkResponse<T>.toApiResult(): ApiResult<T> = when (this) {
    is NetworkResponse.Success -> ApiResult.Success(data)
    is NetworkResponse.Error -> ApiResult.Failure(error)
}

/**
 * Maps the success data of a [NetworkResponse] to a new type.
 */
inline fun <T, R> NetworkResponse<T>.mapSuccess(
    transform: (T) -> R,
): NetworkResponse<R> = when (this) {
    is NetworkResponse.Success -> NetworkResponse.Success(
        meta = meta,
        message = message,
        data = transform(data),
    )
    is NetworkResponse.Error -> this
}

/**
 * Runs [action] if the response is a success.
 */
inline fun <T> NetworkResponse<T>.onSuccess(
    action: (T) -> Unit,
): NetworkResponse<T> = apply {
    if (this is NetworkResponse.Success) action(data)
}

/**
 * Runs [action] if the response is an error.
 */
inline fun <T> NetworkResponse<T>.onError(
    action: (NetworkResponse.Error) -> Unit,
): NetworkResponse<T> = apply {
    if (this is NetworkResponse.Error) action(this)
}
