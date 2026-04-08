package com.application.echo.api.manager

import android.content.SharedPreferences
import android.os.Build
import com.application.echo.core.common.platform.base.BaseDiskSource
import com.application.echo.core.common.repository.bufferedMutableSharedFlow
import com.application.echo.core.network.model.TokenData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.time.ExperimentalTime

private const val ACCESS_TOKEN_KEY = "access_token_key"
private const val REFRESH_TOKEN_KEY = "refresh_token_key"
private const val EXPIRES_AT_KEY = "expires_at_key"

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

    private var expiresAt: String?
        get() = getString(EXPIRES_AT_KEY)
        set(value) {
            putString(EXPIRES_AT_KEY, value)
        }

    private val _tokenDataFlow = bufferedMutableSharedFlow<TokenData?>()

    override val isTokenValid: Boolean
        get() {
            val tokenData = getLatestAuthTokenData()
            return tokenData != null && isTokenDataValid(tokenData)
        }

    override val tokenDataFlow: Flow<TokenData?>
        get() = _tokenDataFlow.onSubscription { emit(getLatestAuthTokenData()) }

    private fun isTokenDataValid(tokenData: TokenData): Boolean {
        val currentTime = System.currentTimeMillis() / 1000 // convert to seconds
        val tokenExpirationTime = parseExpiresAt(tokenData.expiresAt)
        return currentTime < tokenExpirationTime
    }
    override fun getLatestAuthTokenData(): TokenData? {
        val accessToken = accessToken
        val refreshToken = refreshToken
        val expiresAt = expiresAt

        return if (accessToken != null && refreshToken != null && expiresAt != null) {
            TokenData(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresAt = expiresAt
            )
        } else {
            null
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun parseExpiresAt(expiresIn: String): Long = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(expiresIn).epochSecond
        } else {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .parse(expiresIn)!!
                .time / 1000
        }
    } catch (e: Exception) { 0L }

    override fun storeTokenData(accessToken: String, refreshToken: String, expiresAt: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.expiresAt = expiresAt
        _tokenDataFlow.tryEmit(
            TokenData(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresAt = expiresAt,
            ),
        )
    }

    override fun clearTokenData() {
        accessToken = null
        refreshToken = null
        expiresAt = null
        _tokenDataFlow.tryEmit(null)
    }
}