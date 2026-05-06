package com.application.echo.features.auth.datasource.disk

import android.content.SharedPreferences
import com.application.echo.core.common.annotations.EncryptedPreferences
import com.application.echo.core.common.annotations.UnencryptedPreferences
import com.application.echo.core.common.platform.base.BaseEncryptedDiskSource
import com.application.echo.core.common.repository.bufferedMutableSharedFlow
import com.application.echo.features.auth.model.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val KEY_USER_STATE = "user_state"
private const val KEY_SESSION_ID = "session_id"
private const val KEY_SESSION_TOKEN = "session_token"
private const val KEY_REGISTER_EMAIL = "register_email_key"
private const val KEY_REGISTER_PASSWORD = "register_password_key"
private const val KEY_REGISTER_PHONE_NUMBER = "register_phone_number_key"

class AuthDiskSourceImpl @Inject constructor(
    @UnencryptedPreferences sharedPreferences: SharedPreferences,
    @EncryptedPreferences encryptedSharedPreferences: SharedPreferences,
    private val json: Json,
) : BaseEncryptedDiskSource(
    sharedPreferences = sharedPreferences,
    encryptedSharedPreferences = encryptedSharedPreferences,
), AuthDiskSource {

    private val _userStateFlow = bufferedMutableSharedFlow<UserState>()

    override var userState: UserState
        get() = getString(KEY_USER_STATE)
            ?.let { runCatching { json.decodeFromString<UserState>(it) }.getOrNull() }
            ?: UserState.Empty
        set(value) {
            putString(KEY_USER_STATE, json.encodeToString(value))
            _userStateFlow.tryEmit(value)
        }

    override val userStateFlow: Flow<UserState>
        get() = _userStateFlow.onSubscription { emit(userState) }

    override var sessionId: String?
        get() = getString(KEY_SESSION_ID)
        set(value) { putString(KEY_SESSION_ID, value) }

    override var sessionToken: String?
        get() = getString(KEY_SESSION_TOKEN)
        set(value) { putString(KEY_SESSION_TOKEN, value) }

    private val _registerEmailStateFlow = bufferedMutableSharedFlow<String?>()

    override var registerEmail: String?
        get() = getEncryptedString(KEY_REGISTER_EMAIL)
        set(value) {
            putEncryptedString(KEY_REGISTER_EMAIL, value)
            _registerEmailStateFlow.tryEmit(value)
        }

    override val registerEmailStateFlow: Flow<String?>
        get() = _registerEmailStateFlow.onSubscription { emit(registerEmail) }

    private val _registerPasswordStateFlow = bufferedMutableSharedFlow<String?>()

    override var registerPassword: String?
        get() = getEncryptedString(KEY_REGISTER_PASSWORD)
        set(value) {
            putEncryptedString(KEY_REGISTER_PASSWORD, value)
            _registerPasswordStateFlow.tryEmit(value)
        }

    override val registerPasswordStateFlow: Flow<String?>
        get() = _registerPasswordStateFlow.onSubscription { emit(registerPassword) }

    private val _registerPhoneNumberStateFlow = bufferedMutableSharedFlow<String?>()

    override var registerPhoneNumber: String?
        get() = getEncryptedString(KEY_REGISTER_PHONE_NUMBER)
        set(value) {
            putEncryptedString(KEY_REGISTER_PHONE_NUMBER, value)
            _registerPhoneNumberStateFlow.tryEmit(value)
        }

    override val registerPhoneNumberStateFlow: Flow<String?>
        get() = _registerPhoneNumberStateFlow.onSubscription { emit(registerPhoneNumber) }
}