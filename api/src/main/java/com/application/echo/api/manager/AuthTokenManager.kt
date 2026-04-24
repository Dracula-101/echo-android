package com.application.echo.api.manager

import com.application.echo.core.network.provider.AuthTokenProvider
import com.application.echo.core.network.model.TokenData
import kotlinx.coroutines.flow.Flow

interface AuthTokenManager : AuthTokenProvider {

    val isTokenValid: Boolean

    val tokenDataFlow: Flow<TokenData?>

    fun storeTokenData(accessToken: String, refreshToken: String, expiresAt: String)

    fun clearTokenData()
}