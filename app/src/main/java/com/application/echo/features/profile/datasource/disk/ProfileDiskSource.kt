package com.application.echo.features.profile.datasource.disk

import com.application.echo.features.profile.model.ProfileCreationState
import com.application.echo.features.profile.model.ProfileState
import kotlinx.coroutines.flow.Flow

interface ProfileDiskSource {

    /** Profile State */
    var profileState: ProfileState
    val profileStateFlow: Flow<ProfileState>

    /** Creating profile state */
    var creatingProfileState: ProfileCreationState
    val creatingProfileStateFlow: Flow<ProfileCreationState>

    /** Creating profile user id */
    var creatingProfileUserId: String?
    val creatingProfileUserIdStateFlow: Flow<String?>

    /** Creating profile display name */
    var creatingProfileDisplayName: String?
    val creatingProfileDisplayNameStateFlow: Flow<String?>

    /** Creating profile first name */
    var creatingProfileFirstName: String?
    val creatingProfileFirstNameStateFlow: Flow<String?>

    /** Creating profile last name */
    var creatingProfileLastName: String?
    val creatingProfileLastNameStateFlow: Flow<String?>

    /** Creating profile date of birth */
    var creatingProfileDateOfBirth: Long?
    val creatingProfileDateOfBirthStateFlow: Flow<Long?>

    /** Creating profile gender */
    var creatingProfileGender: String?
    val creatingProfileGenderStateFlow: Flow<String?>

    fun startCreatingProfile(userId: String)
    fun finishCreatingProfile()
}