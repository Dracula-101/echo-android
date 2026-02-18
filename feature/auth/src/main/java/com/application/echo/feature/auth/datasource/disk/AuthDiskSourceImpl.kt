package com.application.echo.feature.auth.datasource.disk

import android.content.SharedPreferences
import com.application.echo.feature.auth.model.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import androidx.core.content.edit
import com.application.echo.core.common.platform.base.BaseDiskSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.serialization.encodeToString

private const val USER_STATE_KEY = "user_state"

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



}