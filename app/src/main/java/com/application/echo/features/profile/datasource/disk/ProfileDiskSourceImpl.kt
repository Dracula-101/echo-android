package com.application.echo.features.profile.datasource.disk

import android.content.SharedPreferences
import com.application.echo.core.common.platform.base.BaseDiskSource
import com.application.echo.features.profile.model.ProfileState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import com.application.echo.core.common.repository.bufferedMutableSharedFlow

private const val PROFILE_STATE_KEY = "profile_state_key"
private const val CREATING_PROFILE_STATE_KEY = "creating_profile_key"
private const val CREATING_PROFILE_PHONE_NUMBER_KEY = "creating_profile_phone_number_key"
private const val CREATING_PROFILE_DISPLAY_NAME_KEY = "creating_profile_display_name_key"
private const val CREATING_PROFILE_USER_FIRST_NAME_KEY = "creating_profile_user_first_name_key"
private const val CREATING_PROFILE_USER_LAST_NAME_KEY = "creating_profile_user_last_name_key"
private const val CREATING_PROFILE_USER_DATE_OF_BIRTH_KEY = "creating_profile_user_date_of_birth_key"
private const val CREATING_PROFILE_USER_GENDER_KEY = "creating_profile_user_gender_key"


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

    private val _creatingProfilePhoneNumberStateFlow = bufferedMutableSharedFlow<String?>()

    override val creatingProfilePhoneNumberStateFlow: Flow<String?>
        get() = _creatingProfilePhoneNumberStateFlow.onSubscription { emit(creatingProfilePhoneNumber) }

    override var creatingProfilePhoneNumber: String?
        get() = getString(CREATING_PROFILE_PHONE_NUMBER_KEY)
        set(value) {
            if (value == null) {
                remove(CREATING_PROFILE_PHONE_NUMBER_KEY)
            } else {
                putString(CREATING_PROFILE_PHONE_NUMBER_KEY, value)
            }
            _creatingProfilePhoneNumberStateFlow.tryEmit(value)
        }

    private val _creatingProfileDisplayNameStateFlow = bufferedMutableSharedFlow<String?>()

    override val creatingProfileDisplayNameStateFlow: Flow<String?>
        get() = _creatingProfileDisplayNameStateFlow.onSubscription { emit(creatingProfileDisplayName) }

    override var creatingProfileDisplayName: String?
        get() = getString(CREATING_PROFILE_DISPLAY_NAME_KEY)
        set(value) {
            if (value == null) {
                remove(CREATING_PROFILE_DISPLAY_NAME_KEY)
            } else {
                putString(CREATING_PROFILE_DISPLAY_NAME_KEY, value)
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

    private val _creatingProfileDateOfBirthStateFlow = bufferedMutableSharedFlow<Long?>()

    override val creatingProfileDateOfBirthStateFlow: Flow<Long?>
        get() = _creatingProfileDateOfBirthStateFlow.onSubscription { emit(creatingProfileDateOfBirth) }

    override var creatingProfileDateOfBirth: Long?
        get() = getLong(CREATING_PROFILE_USER_DATE_OF_BIRTH_KEY)
        set(value) {
            if (value == null) {
                remove(CREATING_PROFILE_USER_DATE_OF_BIRTH_KEY)
            } else {
                putLong(CREATING_PROFILE_USER_DATE_OF_BIRTH_KEY, value)
            }
            _creatingProfileDateOfBirthStateFlow.tryEmit(value)
        }

    private val _creatingProfileGenderStateFlow = bufferedMutableSharedFlow<String?>()

    override val creatingProfileGenderStateFlow: Flow<String?>
        get() = _creatingProfileGenderStateFlow.onSubscription { emit(creatingProfileGender)
        }

    override var creatingProfileGender: String?
        get() = getString(CREATING_PROFILE_USER_GENDER_KEY)
        set(value) {
            if (value == null) {
                remove(CREATING_PROFILE_USER_GENDER_KEY)
            } else {
                putString(CREATING_PROFILE_USER_GENDER_KEY, value)
            }
            _creatingProfileGenderStateFlow.tryEmit(value)
        }

    override fun startCreatingProfile(phoneNumber: String) {
        creatingProfilePhoneNumber = phoneNumber
        creatingProfileState = true
    }

    override fun finishCreatingProfile() {
        creatingProfilePhoneNumber = null
        creatingProfileState = false
        creatingProfileFirstName = null
        creatingProfileLastName = null
    }

}