package com.application.echo.features.auth.datasource.disk

import android.content.SharedPreferences
import com.application.echo.core.common.annotations.EncryptedPreferences
import com.application.echo.core.common.annotations.UnencryptedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import com.application.echo.core.common.platform.base.BaseDiskSource
import com.application.echo.core.common.platform.base.BaseEncryptedDiskSource
import com.application.echo.core.common.repository.bufferedMutableSharedFlow
import com.application.echo.features.auth.model.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.serialization.encodeToString

private const val USER_STATE_KEY = "user_state"
private const val SESSION_ID_KEY = "session_id"
private const val SESSION_TOKEN_KEY = "session_token"
private const val REGISTER_EMAIL_KEY = "register_email_key"
private const val REGISTER_PASSWORD_KEY = "register_password_key"

class AuthDiskSourceImpl @Inject constructor(
    @UnencryptedPreferences sharedPreferences: SharedPreferences,
    @EncryptedPreferences encryptedSharedPreferences: SharedPreferences,
    private val json: Json,
) : BaseEncryptedDiskSource(
    sharedPreferences = sharedPreferences,
    encryptedSharedPreferences = encryptedSharedPreferences
), AuthDiskSource {

    private val _userStateFlow = bufferedMutableSharedFlow<UserState>()

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


    private val _registerEmailStateFlow = bufferedMutableSharedFlow<String?>()

    override var registerEmail: String?
        get() = getEncryptedString(REGISTER_EMAIL_KEY)
        set(value) {
            putEncryptedString(REGISTER_EMAIL_KEY, value)
            _registerEmailStateFlow.tryEmit(value)
        }

    override val registerEmailStateFlow: Flow<String?>
        get() = _registerEmailStateFlow


    private val _registerPasswordStateFlow = bufferedMutableSharedFlow<String?>()

    override var registerPassword: String?
        get() = getEncryptedString(REGISTER_PASSWORD_KEY)
        set(value) {
            putEncryptedString(REGISTER_PASSWORD_KEY, value)
            _registerPasswordStateFlow.tryEmit(value)
        }

    override val registerPasswordStateFlow: Flow<String?>
        get() = _registerPasswordStateFlow.onSubscription { emit(registerPassword) }

}