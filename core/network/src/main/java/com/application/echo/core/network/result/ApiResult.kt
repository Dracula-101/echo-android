package com.application.echo.core.network.result

import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.model.NetworkResponse

/**
 * A simplified result type for consuming API responses.
 *
 * Strips the [Meta] envelope and exposes only the data or the error,
 * making it ideal for ViewModels and repository layers that don't
 * care about transport-level metadata.
 *
 * Convert from [NetworkResponse] via [toApiResult].
 */
sealed interface ApiResult<out T> {

    data class Success<T>(val data: T) : ApiResult<T>

    data class Failure(val exception: NetworkException) : ApiResult<Nothing>
}

/**
 * Returns `true` if this result is a [ApiResult.Success].
 */
val <T> ApiResult<T>.isSuccess: Boolean
    get() = this is ApiResult.Success

/**
 * Returns `true` if this result is a [ApiResult.Failure].
 */
val <T> ApiResult<T>.isFailure: Boolean
    get() = this is ApiResult.Failure

/**
 * Returns the success data or `null`.
 */
fun <T> ApiResult<T>.getOrNull(): T? = when (this) {
    is ApiResult.Success -> data
    is ApiResult.Failure -> null
}

/**
 * Returns the success data or throws the underlying exception.
 */
fun <T> ApiResult<T>.getOrThrow(): T = when (this) {
    is ApiResult.Success -> data
    is ApiResult.Failure -> throw exception.throwable
}

/**
 * Returns the success data or the [default] value.
 */
fun <T> ApiResult<T>.getOrDefault(default: T): T = when (this) {
    is ApiResult.Success -> data
    is ApiResult.Failure -> default
}

/**
 * Returns the [NetworkException] or `null`.
 */
fun <T> ApiResult<T>.exceptionOrNull(): NetworkException? = when (this) {
    is ApiResult.Success -> null
    is ApiResult.Failure -> exception
}

/**
 * Transforms the success data.
 */
inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Success -> ApiResult.Success(transform(data))
    is ApiResult.Failure -> this
}

/**
 * Flat-maps the success data to another [ApiResult].
 */
inline fun <T, R> ApiResult<T>.flatMap(transform: (T) -> ApiResult<R>): ApiResult<R> = when (this) {
    is ApiResult.Success -> transform(data)
    is ApiResult.Failure -> this
}

/**
 * Runs [action] if successful, returns the original result unchanged.
 */
inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> = apply {
    if (this is ApiResult.Success) action(data)
}

/**
 * Runs [action] if failed, returns the original result unchanged.
 */
inline fun <T> ApiResult<T>.onFailure(action: (NetworkException) -> Unit): ApiResult<T> = apply {
    if (this is ApiResult.Failure) action(exception)
}

/**
 * Folds into a single value regardless of outcome.
 */
inline fun <T, R> ApiResult<T>.fold(
    onSuccess: (T) -> R,
    onFailure: (NetworkException) -> R,
): R = when (this) {
    is ApiResult.Success -> onSuccess(data)
    is ApiResult.Failure -> onFailure(exception)
}
