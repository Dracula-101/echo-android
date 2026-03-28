package com.application.echo.features.profile.repository

import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.model.ProfileState
import kotlinx.coroutines.flow.Flow


interface ProfileRepository {

    val profileStateFlow: Flow<ProfileState>

    val creatingProfileStateFlow: Flow<CreatingProfileState>

}