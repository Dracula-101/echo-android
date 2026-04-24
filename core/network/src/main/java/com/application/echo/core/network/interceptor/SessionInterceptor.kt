package com.application.echo.core.network.interceptor

import com.application.echo.core.network.provider.SessionProvider
import com.application.echo.core.network.util.HeaderConstants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class SessionHeaderInterceptor @Inject constructor(
    private val sessionProvider: SessionProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        // Session headers (only when authenticated)
        sessionProvider.sessionId?.let {
            builder.header(HeaderConstants.SESSION_ID, it)
        }
        sessionProvider.sessionToken?.let {
            builder.header(HeaderConstants.SESSION_TOKEN, it)
        }

        return chain.proceed(builder.build())
    }
}