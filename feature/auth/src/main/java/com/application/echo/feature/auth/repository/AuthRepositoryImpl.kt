package com.application.echo.feature.auth.repository

import com.application.echo.core.api.auth.AuthLoginResult
import com.application.echo.core.api.auth.AuthRegisterResult
import com.application.echo.core.api.auth.SessionInfo
import com.application.echo.core.api.auth.onSuccess
import com.application.echo.core.api.manager.AuthTokenManager
import com.application.echo.feature.auth.datasource.disk.AuthDiskSource
import com.application.echo.feature.auth.datasource.network.AuthNetworkSource
import com.application.echo.feature.auth.model.UserState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val DEFAULT_TOKEN_LIFETIME_SECONDS = 86_400L // 24 hours

class AuthRepositoryImpl @Inject constructor(
    private val networkSource: AuthNetworkSource,
    private val diskSource: AuthDiskSource,
    private val tokenManager: AuthTokenManager,
) : AuthRepository {

    override val userStateFlow: Flow<UserState>
        get() = diskSource.userStateFlow

    override suspend fun login(
        email: String,
        password: String,
    ): AuthLoginResult {
        val result = networkSource.login(email, password)
        result.onSuccess { response ->
            persistSession(response.session, response.user.id, response.user.email)
        }
        return result
    }

    override suspend fun register(
        email: String,
        password: String,
        acceptTerms: Boolean,
    ): AuthRegisterResult {
        val result = networkSource.register(email, password, acceptTerms)
        result.onSuccess { response ->
            persistSession(response.session, response.user.id, response.user.email)
        }
        return result
    }

    override fun logout() {
        tokenManager.clearTokenData()
        diskSource.sessionId = null
        diskSource.sessionToken = null
        diskSource.userState = UserState.Empty
    }

    private fun persistSession(session: SessionInfo, userId: String, email: String) {
        val expiresAt = (System.currentTimeMillis() / 1_000) + DEFAULT_TOKEN_LIFETIME_SECONDS
        tokenManager.storeTokenData(
            accessToken = session.accessToken,
            refreshToken = session.refreshToken,
            expiresIn = expiresAt,
        )
        diskSource.sessionId = session.sessionId
        diskSource.sessionToken = session.sessionToken
        diskSource.userState = UserState(
            userId = userId,
            email = email,
        )
    }
}
