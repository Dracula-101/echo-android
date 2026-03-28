package com.application.echo.features.auth.datasource.disk

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import com.application.echo.core.common.platform.base.BaseDiskSource
import com.application.echo.features.auth.model.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.serialization.encodeToString

private const val USER_STATE_KEY = "user_state"
private const val SESSION_ID_KEY = "session_id"
private const val SESSION_TOKEN_KEY = "session_token"

class AuthDiskSourceImpl @Inject constructor(
    sharedPreferences: SharedPreferences,
    private val json: Json,
) : BaseDiskSource(
    sharedPreferences = sharedPreferences,
), AuthDiskSource {

    private val _userStateFlow = MutableStateFlow(UserState.Empty)

    override var userState: UserState
        get() = getString(USER_STATE_KEY)?.let { json.decodeFromString<UserState>(it) } ?: UserState.Empty
        set(value) {
            putString(
                key = USER_STATE_KEY,
                value = json.encodeToString(value)
            )
            _userStateFlow.tryEmit(value)
        }

    override val userStateFlow: Flow<UserState>
        get() = _userStateFlow.onSubscription { emit(userState) }

    override var sessionId: String?
        get() = getString(SESSION_ID_KEY)
        set(value) {
            putString(SESSION_ID_KEY, value)
        }

    override var sessionToken: String?
        get() = getString(SESSION_TOKEN_KEY)
        set(value) {
            putString(SESSION_TOKEN_KEY, value)
        }
}