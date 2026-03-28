package com.application.echo.features.profile.datasource.disk

import com.application.echo.features.profile.model.ProfileState
import kotlinx.coroutines.flow.Flow

interface ProfileDiskSource {

    var profileState: ProfileState

    val profileStateFlow: Flow<ProfileState>

    var creatingProfileState: Boolean

    val creatingProfileStateFlow: Flow<Boolean>

    val creatingProfileUserIdStateFlow: Flow<String?>

    var creatingProfileUserId: String?

    fun startCreatingProfile(userId: String)

    fun finishCreatingProfile(userId: String)
}