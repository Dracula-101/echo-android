package com.application.echo.features.profile.datasource.disk

import com.application.echo.features.profile.model.ProfileState
import kotlinx.coroutines.flow.Flow

interface ProfileDiskSource {

    /** Profile State */
    var profileState: ProfileState
    val profileStateFlow: Flow<ProfileState>


    /** Creating profile state */
    var creatingProfileState: Boolean
    val creatingProfileStateFlow: Flow<Boolean>


    /** Creating profile user id */
    var creatingProfilePhoneNumber: String?
    val creatingProfilePhoneNumberStateFlow: Flow<String?>


    var creatingProfileDisplayName: String?
    val creatingProfileDisplayNameStateFlow: Flow<String?>


    var creatingProfileFirstName: String?
    val creatingProfileFirstNameStateFlow: Flow<String?>


    var creatingProfileLastName: String?
    val creatingProfileLastNameStateFlow: Flow<String?>


    var creatingProfileAvatarUrl: String?
    val creatingProfileAvatarUrlStateFlow: Flow<String?>


    fun startCreatingProfile(phoneNumber: String)
    fun finishCreatingProfile()
}