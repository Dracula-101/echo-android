package com.application.echo.features.profile.datasource.disk

import android.content.SharedPreferences
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

private const val PROFILE_STATE_KEY = "profile_state"
private const val CREATING_PROFILE_STATE_KEY = "creating_profile_key"
private const val CREATING_PROFILE_USER_ID_KEY = "creating_profile_user_id_key"


class ProfileDiskSourceImpl @Inject constructor(
    sharedPreferences: SharedPreferences,
    private val json: Json,
) : BaseDiskSource(sharedPreferences = sharedPreferences), ProfileDiskSource {

    /** Profile State */
    private val _profileStateFlow = MutableStateFlow(ProfileState.Empty)

    override var profileState: ProfileState
        get() = getString(PROFILE_STATE_KEY)?.let { json.decodeFromString<ProfileState>(it) } ?: ProfileState.Empty
        set(value) {
            putString(
                key = PROFILE_STATE_KEY,
                value = json.encodeToString(value)
            )
            _profileStateFlow.value = value
        }

    override val profileStateFlow: Flow<ProfileState>
        get() = _profileStateFlow.onSubscription { emit(profileState) }


    /** Creating profile state */
    private val _creatingProfileStateFlow = MutableStateFlow(false)

    override var creatingProfileState: Boolean
        get() = getBoolean(CREATING_PROFILE_STATE_KEY) ?: false
        set(value) {
            putBoolean(CREATING_PROFILE_STATE_KEY, value)
            _creatingProfileStateFlow.value = value
        }

    override val creatingProfileStateFlow: Flow<Boolean>
        get() = _creatingProfileStateFlow.onSubscription { emit(creatingProfileState) }

    private val _creatingProfileUserIdStateFlow = MutableStateFlow<String?>(null)

    override val creatingProfileUserIdStateFlow: Flow<String?>
        get() = _creatingProfileUserIdStateFlow.onSubscription { emit(creatingProfileUserId) }

    override var creatingProfileUserId: String?
        get() = getString(CREATING_PROFILE_USER_ID_KEY)
        set(value) {
            putString(CREATING_PROFILE_USER_ID_KEY, value)
            _creatingProfileUserIdStateFlow.value = value
        }

    override fun startCreatingProfile(userId: String) {
        creatingProfileUserId = userId
        creatingProfileState = true
    }

    override fun finishCreatingProfile(userId: String) {
        creatingProfileUserId = null
        creatingProfileState = false
    }

}