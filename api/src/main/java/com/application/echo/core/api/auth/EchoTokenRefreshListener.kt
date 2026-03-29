package com.application.echo.core.api.auth

import com.application.echo.core.api.manager.AuthTokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EchoTokenRefreshListener @Inject constructor(
    private val authTokenManager: AuthTokenManager,
) : TokenRefreshListener {

    override fun onTokenRefreshed(accessToken: String, refreshToken: String, expiresAt: String) {
        authTokenManager.storeTokenData(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresAt = expiresAt
        )
    }

    override fun onRefreshFailed() {
        authTokenManager.clearTokenData()
    }

    override fun getRefreshToken(): String? =
        authTokenManager.getLatestAuthTokenData()?.refreshToken
}