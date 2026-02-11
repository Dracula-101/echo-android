package com.application.echo.core.api.auth

import com.application.echo.core.network.result.ApiResult
import com.application.echo.core.network.result.toApiResult
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

/**
 * OkHttp [Authenticator] that automatically refreshes the access token
 * when a 401 response is received.
 *
 * **How it works:**
 * 1. OkHttp calls [authenticate] when any request returns 401.
 * 2. This class calls [AuthApiService.refreshToken] to get new tokens.
 * 3. Notifies [TokenRefreshListener] so the app can persist the new tokens.
 * 4. Retries the original request with the new access token.
 *
 * A [Mutex] prevents multiple concurrent 401s from triggering
 * multiple refresh calls — only the first refreshes, the rest reuse the result.
 *
 * **Setup:**
 * Implement [TokenRefreshListener] in your app module:
 * ```kotlin
 * @Singleton
 * class EchoTokenRefreshListener @Inject constructor(
 *     private val sessionStore: SessionStore,
 * ) : TokenRefreshListener {
 *     override fun onTokenRefreshed(accessToken: String, refreshToken: String) {
 *         sessionStore.saveTokens(accessToken, refreshToken)
 *     }
 *     override fun onRefreshFailed() {
 *         sessionStore.clearSession()
 *         // Navigate to login
 *     }
 *     override fun getRefreshToken(): String? = sessionStore.refreshToken
 * }
 * ```
 */
internal class TokenRefreshAuthenticator @Inject constructor(
    private val authApi: AuthApiService,
    private val listener: TokenRefreshListener,
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Bail if we've already retried once (prevent infinite loops)
        if (response.request.header(RETRY_HEADER) != null) {
            Timber.tag(TAG).w("Token refresh already attempted, giving up")
            listener.onRefreshFailed()
            return null
        }

        return runBlocking {
            mutex.withLock {
                val refreshToken = listener.getRefreshToken()
                if (refreshToken == null) {
                    Timber.tag(TAG).w("No refresh token available")
                    listener.onRefreshFailed()
                    return@runBlocking null
                }

                Timber.tag(TAG).d("Refreshing access token…")
                val result = authApi.refreshToken(
                    RefreshTokenRequest(refreshToken),
                ).toApiResult()

                when (result) {
                    is ApiResult.Success -> {
                        val data = result.data
                        Timber.tag(TAG).d("Token refreshed successfully")
                        listener.onTokenRefreshed(data.accessToken, data.refreshToken)

                        response.request.newBuilder()
                            .header(AUTHORIZATION_HEADER, "Bearer ${data.accessToken}")
                            .header(RETRY_HEADER, "true")
                            .build()
                    }

                    is ApiResult.Failure -> {
                        Timber.tag(TAG).e("Token refresh failed: ${result.exception}")
                        listener.onRefreshFailed()
                        null
                    }
                }
            }
        }
    }

    private companion object {
        const val TAG = "TokenRefresh"
        const val AUTHORIZATION_HEADER = "Authorization"
        const val RETRY_HEADER = "X-Token-Refresh-Retry"
    }
}

/**
 * Listener for token refresh events.
 *
 * Implement this in your app module to persist tokens and handle logout.
 */
interface TokenRefreshListener {

    /** Called when the refresh succeeds. Persist the new tokens. */
    fun onTokenRefreshed(accessToken: String, refreshToken: String)

    /** Called when the refresh fails. Clear session and navigate to login. */
    fun onRefreshFailed()

    /** Returns the current stored refresh token, or `null` if not authenticated. */
    fun getRefreshToken(): String?
}
