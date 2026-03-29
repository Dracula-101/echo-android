package com.application.echo.features.profile.datasource.disk

import android.content.SharedPreferences
import android.net.Uri
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.core.common.platform.base.BaseDiskSource
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.profile.model.ProfileState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import androidx.core.net.toUri
import com.application.echo.core.common.annotations.EncryptedPreferences
import com.application.echo.core.common.platform.base.BaseEncryptedDiskSource
import com.application.echo.core.common.repository.bufferedMutableSharedFlow

private const val PROFILE_STATE_KEY = "profile_state_key"
private const val CREATING_PROFILE_STATE_KEY = "creating_profile_key"
private const val CREATING_PROFILE_USER_ID_KEY = "creating_profile_user_id_key"
private const val CREATING_PROFILE_USER_DISPLAY_NAME_KEY = "creating_profile_user_display_name_key"
private const val CREATING_PROFILE_USER_FIRST_NAME_KEY = "creating_profile_user_first_name_key"
private const val CREATING_PROFILE_USER_LAST_NAME_KEY = "creating_profile_user_last_name_key"
private const val CREATING_PROFILE_USER_AVATAR_URL_KEY = "creating_profile_user_avatar_url_key"


class ProfileDiskSourceImpl @Inject constructor(
    sharedPreferences: SharedPreferences,
    private val json: Json,
) : BaseDiskSource(sharedPreferences = sharedPreferences), ProfileDiskSource {

    /** Profile State */
    private val _profileStateFlow = bufferedMutableSharedFlow<ProfileState>()

    override var profileState: ProfileState
        get() = getString(PROFILE_STATE_KEY)?.let { json.decodeFromString<ProfileState>(it) } ?: ProfileState.Empty
        set(value) {
            putString(
                key = PROFILE_STATE_KEY,
                value = json.encodeToString(value)
            )
            _profileStateFlow.tryEmit(value)
        }

    override val profileStateFlow: Flow<ProfileState>
        get() = _profileStateFlow.onSubscription { emit(profileState) }


    /** Creating profile state */
    private val _creatingProfileStateFlow = bufferedMutableSharedFlow<Boolean>()

    override var creatingProfileState: Boolean
        get() = getBoolean(CREATING_PROFILE_STATE_KEY) ?: false
        set(value) {
            putBoolean(CREATING_PROFILE_STATE_KEY, value)
            _creatingProfileStateFlow.tryEmit(value)
        }

    override val creatingProfileStateFlow: Flow<Boolean>
        get() = _creatingProfileStateFlow.onSubscription { emit(creatingProfileState) }

    private val _creatingProfileUserIdStateFlow = bufferedMutableSharedFlow<String?>()

    override val creatingProfileUserIdStateFlow: Flow<String?>
        get() = _creatingProfileUserIdStateFlow.onSubscription { emit(creatingProfileUserId) }

    override var creatingProfileUserId: String?
        get() = getString(CREATING_PROFILE_USER_ID_KEY)
        set(value) {
            if (value == null) {
                remove(CREATING_PROFILE_USER_ID_KEY)
            } else {
                putString(CREATING_PROFILE_USER_ID_KEY, value)
            }
            _creatingProfileUserIdStateFlow.tryEmit(value)
        }

    private val _creatingProfileDisplayNameStateFlow = bufferedMutableSharedFlow<String?>()

    override val creatingProfileDisplayNameStateFlow: Flow<String?>
        get() = _creatingProfileDisplayNameStateFlow.onSubscription { emit(creatingProfileDisplayName) }

    override var creatingProfileDisplayName: String?
        get() = getString(CREATING_PROFILE_USER_DISPLAY_NAME_KEY)
        set(value) {
            if (value == null) {
                remove(CREATING_PROFILE_USER_DISPLAY_NAME_KEY)
            } else {
                putString(CREATING_PROFILE_USER_DISPLAY_NAME_KEY, value)
            }
            _creatingProfileDisplayNameStateFlow.tryEmit(value)
        }

    private val _creatingProfileFirstNameStateFlow = bufferedMutableSharedFlow<String?>()

    override val creatingProfileFirstNameStateFlow: Flow<String?>
        get() = _creatingProfileFirstNameStateFlow.onSubscription { emit(creatingProfileFirstName) }

    override var creatingProfileFirstName: String?
        get() = getString(CREATING_PROFILE_USER_FIRST_NAME_KEY)
        set(value) {
            if (value == null) {
                remove(CREATING_PROFILE_USER_FIRST_NAME_KEY)
            } else {
                putString(CREATING_PROFILE_USER_FIRST_NAME_KEY, value)
            }
            _creatingProfileFirstNameStateFlow.tryEmit(value)
        }

    private val _creatingProfileLastNameStateFlow = bufferedMutableSharedFlow<String?>()

    override val creatingProfileLastNameStateFlow: Flow<String?>
        get() = _creatingProfileLastNameStateFlow.onSubscription { emit(creatingProfileLastName) }

    override var creatingProfileLastName: String?
        get() = getString(CREATING_PROFILE_USER_LAST_NAME_KEY)
        set(value) {
            if (value == null) {
                remove(CREATING_PROFILE_USER_LAST_NAME_KEY)
            } else {
                putString(CREATING_PROFILE_USER_LAST_NAME_KEY, value)
            }
            _creatingProfileLastNameStateFlow.tryEmit(value)
        }

    private val _creatingProfileAvatarUrlStateFlow = bufferedMutableSharedFlow<String?>()

    override val creatingProfileAvatarUrlStateFlow: Flow<String?>
        get() = _creatingProfileAvatarUrlStateFlow.onSubscription { emit(creatingProfileAvatarUrl) }

    override var creatingProfileAvatarUrl: String?
        get() = getString(CREATING_PROFILE_USER_AVATAR_URL_KEY)
        set(value) {
            if (value == null) {
                remove(CREATING_PROFILE_USER_AVATAR_URL_KEY)
            } else {
                putString(CREATING_PROFILE_USER_AVATAR_URL_KEY, value)
            }
            _creatingProfileAvatarUrlStateFlow.tryEmit(value)
        }



    override fun startCreatingProfile(userId: String) {
        creatingProfileUserId = userId
        creatingProfileState = true
    }

    override fun finishCreatingProfile() {
        creatingProfileUserId = null
        creatingProfileState = false
        creatingProfileDisplayName = null
        creatingProfileFirstName = null
        creatingProfileLastName = null
        creatingProfileAvatarUrl = null
    }

}