package com.application.echo.features.profile.repository

import android.net.Uri
import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.model.ProfileResult
import com.application.echo.features.profile.model.ProfileState
import kotlinx.coroutines.flow.Flow


interface ProfileRepository {

    val profileStateFlow: Flow<ProfileState>

    val creatingProfileStateFlow: Flow<CreatingProfileState>

    suspend fun setProfileInfo(
        userId: String,
        displayName: String,
        firstName: String,
        lastName: String,
        avatarUri: Uri
    ): ProfileResult<Unit>

}