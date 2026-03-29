package com.application.echo.features.profile.model

import com.application.echo.core.api.profile.ProfileError
import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.result.ApiResult

/**
 * Domain-specific result type for profile operations.
 *
 * Replaces [ApiResult] at the repository boundary so consumers
 * get typed [ProfileError] instead of raw [NetworkException].
 */
sealed interface ProfileResult<out T> {

    data class Success<T>(val data: T) : ProfileResult<T>

    data class Error(val error: ProfileError) : ProfileResult<Nothing>
}

// ── Helpers ──────────────────────────────────────────────────────────

val <T> ProfileResult<T>.isSuccess: Boolean
    get() = this is ProfileResult.Success

val <T> ProfileResult<T>.isError: Boolean
    get() = this is ProfileResult.Error

fun <T> ProfileResult<T>.getOrNull(): T? = when (this) {
    is ProfileResult.Success -> data
    is ProfileResult.Error -> null
}

fun <T> ProfileResult<T>.errorOrNull(): ProfileError? = when (this) {
    is ProfileResult.Success -> null
    is ProfileResult.Error -> error
}

inline fun <T> ProfileResult<T>.onSuccess(action: (T) -> Unit): ProfileResult<T> = apply {
    if (this is ProfileResult.Success) action(data)
}

inline fun <T> ProfileResult<T>.onError(action: (ProfileError) -> Unit): ProfileResult<T> = apply {
    if (this is ProfileResult.Error) action(error)
}

inline fun <T, R> ProfileResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (ProfileError) -> R,
): R = when (this) {
    is ProfileResult.Success -> onSuccess(data)
    is ProfileResult.Error -> onError(error)
}

inline fun <T, R> ProfileResult<T>.map(transform: (T) -> R): ProfileResult<R> = when (this) {
    is ProfileResult.Success -> ProfileResult.Success(transform(data))
    is ProfileResult.Error -> this
}

// ── Conversion from ApiResult ────────────────────────────────────────

/**
 * Converts an [ApiResult] to a [ProfileResult] by mapping
 * [NetworkException] → [ProfileError].
 */
fun <T> ApiResult<T>.toProfileResult(): ProfileResult<T> = when (this) {
    is ApiResult.Success -> ProfileResult.Success(data)
    is ApiResult.Failure -> ProfileResult.Error(ProfileError.fromNetworkException(exception))
}