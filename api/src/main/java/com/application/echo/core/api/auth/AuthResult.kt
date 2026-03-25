package com.application.echo.core.api.auth

import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.result.ApiResult

/**
 * Domain-specific result type for authentication operations.
 *
 * Replaces [ApiResult] at the repository boundary so consumers
 * get typed [AuthError] instead of raw [NetworkException].
 *
 * ```
 * when (val result = authRepo.login(email, password)) {
 *     is AuthResult.Success -> handleUser(result.data.user)
 *     is AuthResult.Error   -> when (result.error) {
 *         is AuthError.InvalidCredentials -> showBadLogin()
 *         is AuthError.AccountLocked      -> showLocked()
 *         ...
 *     }
 * }
 * ```
 */
sealed interface AuthResult<out T> {

    data class Success<T>(val data: T) : AuthResult<T>

    data class Error(val error: AuthError) : AuthResult<Nothing>
}

// ── Helpers ──────────────────────────────────────────────────────────

val <T> AuthResult<T>.isSuccess: Boolean
    get() = this is AuthResult.Success

val <T> AuthResult<T>.isError: Boolean
    get() = this is AuthResult.Error

fun <T> AuthResult<T>.getOrNull(): T? = when (this) {
    is AuthResult.Success -> data
    is AuthResult.Error -> null
}

fun <T> AuthResult<T>.errorOrNull(): AuthError? = when (this) {
    is AuthResult.Success -> null
    is AuthResult.Error -> error
}

inline fun <T> AuthResult<T>.onSuccess(action: (T) -> Unit): AuthResult<T> = apply {
    if (this is AuthResult.Success) action(data)
}

inline fun <T> AuthResult<T>.onError(action: (AuthError) -> Unit): AuthResult<T> = apply {
    if (this is AuthResult.Error) action(error)
}

inline fun <T, R> AuthResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (AuthError) -> R,
): R = when (this) {
    is AuthResult.Success -> onSuccess(data)
    is AuthResult.Error -> onError(error)
}

inline fun <T, R> AuthResult<T>.map(transform: (T) -> R): AuthResult<R> = when (this) {
    is AuthResult.Success -> AuthResult.Success(transform(data))
    is AuthResult.Error -> this
}

// ── Conversion from ApiResult ────────────────────────────────────────

/**
 * Converts an [ApiResult] to an [AuthResult] by mapping
 * [NetworkException] → [AuthError].
 */
fun <T> ApiResult<T>.toAuthResult(): AuthResult<T> = when (this) {
    is ApiResult.Success -> AuthResult.Success(data)
    is ApiResult.Failure -> AuthResult.Error(AuthError.fromNetworkException(exception))
}
