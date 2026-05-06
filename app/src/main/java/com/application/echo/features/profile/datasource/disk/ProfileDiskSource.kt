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

    fun startCreatingProfile(phoneNumber: String)
    fun finishCreatingProfile()
}