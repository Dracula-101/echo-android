package com.application.echo.core.network.interceptor

import com.application.echo.core.network.util.HeaderConstants
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

/**
 * OkHttp [Interceptor] that attaches a Bearer token to every request.
 *
 * If no token is available the request proceeds without an Authorization
 * header — endpoints that require auth will respond with 401 and the
 * error is handled by the call adapter layer.
 */
internal class AuthInterceptor(
    private val tokenProvider: AuthTokenProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val tokenData = tokenProvider.getLatestAuthTokenData()

        val request = if (tokenData != null) {
            original.newBuilder()
                .header(HeaderConstants.AUTHORIZATION, "${HeaderConstants.BEARER} ${tokenData.accessToken}")
                .build()
        } else {
            Timber.d("No auth token available, proceeding without Authorization header")
            original
        }

        return chain.proceed(request)
    }
}
