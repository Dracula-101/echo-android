package com.application.echo.core.api.manager

import com.application.echo.core.network.interceptor.AuthTokenProvider
import com.application.echo.core.network.model.TokenData
import kotlinx.coroutines.flow.Flow

interface AuthTokenManager: AuthTokenProvider {

    val isTokenValid: Boolean

    val tokenDataFlow: Flow<TokenData?>

    fun clearTokenData()

}