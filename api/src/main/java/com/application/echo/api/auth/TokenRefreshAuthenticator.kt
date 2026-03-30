package com.application.echo.api.auth

import com.application.echo.api.common.ApiConstants.AUTH_REFRESH_TOKEN
import com.application.echo.core.network.client.HttpClientConfig
import com.application.echo.core.network.model.TokenData
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class TokenRefreshAuthenticator @Inject constructor(
    private val config: HttpClientConfig,
    private val gson: Gson,
    private val listener: TokenRefreshListener,
) : Authenticator {

    private val refreshClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header(RETRY_HEADER) != null) {
            listener.onRefreshFailed()
            return null
        }

        return runBlocking {
            mutex.withLock {
                val refreshToken = listener.getRefreshToken() ?: run {
                    listener.onRefreshFailed()
                    return@runBlocking null
                }

                val tokenData = callRefreshToken(refreshToken)

                if (tokenData != null) {
                    listener.onTokenRefreshed(
                        accessToken  = tokenData.accessToken,
                        refreshToken = tokenData.refreshToken,
                        expiresAt    = tokenData.expiresAt,
                    )
                    response.request.newBuilder()
                        .header(AUTHORIZATION_HEADER, "Bearer ${tokenData.accessToken}")
                        .header(RETRY_HEADER, "true")
                        .build()
                } else {
                    listener.onRefreshFailed()
                    null
                }
            }
        }
    }

    private fun callRefreshToken(refreshToken: String): TokenData? {
        return try {
            val body = gson.toJson(RefreshTokenRequest(refreshToken))
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("${config.baseUrl}/$AUTH_REFRESH_TOKEN")
                .post(body)
                .build()

            val response = refreshClient.newCall(request).execute()

            if (response.isSuccessful) {
                val bodyString = response.body?.string() ?: return null
                val wrapper = gson.fromJson(bodyString, RefreshTokenWrapper::class.java)
                TokenData(
                    accessToken  = wrapper.data.accessToken,
                    refreshToken = wrapper.data.refreshToken,
                    expiresAt    = wrapper.data.expiresAt,
                )
            } else {
                Timber.tag(TAG).e("Refresh failed: ${response.code}")
                null
            }

        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Token refresh failed")
            null
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
    fun onTokenRefreshed(accessToken: String, refreshToken: String, expiresAt: String)

    /** Called when the refresh fails. Clear session and navigate to login. */
    fun onRefreshFailed()

    /** Returns the current stored refresh token, or `null` if not authenticated. */
    fun getRefreshToken(): String?
}
