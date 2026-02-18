package com.application.echo.core.api.manager

import android.content.SharedPreferences
import com.application.echo.core.common.platform.base.BaseDiskSource
import com.application.echo.core.network.model.TokenData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import javax.inject.Inject

private const val ACCESS_TOKEN_KEY = "access_token"
private const val REFRESH_TOKEN_KEY = "refresh_token"
private const val EXPIRES_IN_KEY = "expires_in"

class AuthTokenManagerImpl @Inject constructor(
    sharedPreferences: SharedPreferences
): BaseDiskSource(
    sharedPreferences = sharedPreferences
), AuthTokenManager {

    private var accessToken: String?
        get() = getString(ACCESS_TOKEN_KEY)
        set(value) {
            putString(ACCESS_TOKEN_KEY, value)
        }

    private var refreshToken: String?
        get() = getString(REFRESH_TOKEN_KEY)
        set(value) {
            putString(REFRESH_TOKEN_KEY, value)
        }

    private var expiresIn: Long?
        get() = getLong(EXPIRES_IN_KEY)
        set(value) {
            putLong(EXPIRES_IN_KEY, value)
        }

    private val _tokenDataFlow = MutableStateFlow<TokenData?>(null)

    override val isTokenValid: Boolean
        get() = isTokenDataValid(getLatestAuthTokenData())

    override val tokenDataFlow: Flow<TokenData?>
        get() = _tokenDataFlow.onSubscription { emit(getLatestAuthTokenData()) }

    private fun isTokenDataValid(tokenData: TokenData?): Boolean {
        val currentTime = System.currentTimeMillis() / 1000 // convert to seconds
        val tokenExpirationTime = tokenData?.expiresIn ?: 0L
        return currentTime < tokenExpirationTime
    }
    override fun getLatestAuthTokenData(): TokenData? {
        val accessToken = accessToken
        val refreshToken = refreshToken
        val expiresIn = expiresIn

        return if (accessToken != null && refreshToken != null && expiresIn != null) {
            TokenData(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = expiresIn
            )
        } else {
            null
        }
    }

    override fun clearTokenData() {
        accessToken = null
        refreshToken = null
        expiresIn = null
        _tokenDataFlow.tryEmit(null)
    }
}