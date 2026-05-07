package com.application.echo.features.profile.repository

import android.net.Uri
import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.model.ProfileResult
import com.application.echo.features.profile.model.ProfileState
import com.application.echo.features.profile.model.ProfileVisibility
import kotlinx.coroutines.flow.Flow


interface ProfileRepository {

    val profileStateFlow: Flow<ProfileState>

    val creatingProfileStateFlow: Flow<CreatingProfileState>

    suspend fun checkUsernameAvailable(username: String): Boolean

    fun saveProfile(
        displayName: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        dateOfBirth: Long? = null,
        gender: String? = null,
    ): ProfileResult<Unit>

    suspend fun createProfile(
        userId: String,
        displayName: String,
        firstName: String,
        lastName: String,
        bio: String,
        userName: String,
        profileVisibility: ProfileVisibility,
        searchable: Boolean,
        pushEnabled: Boolean,
    ): ProfileResult<Unit>

}