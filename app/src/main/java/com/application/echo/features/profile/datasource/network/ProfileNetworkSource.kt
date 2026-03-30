package com.application.echo.features.profile.datasource.network

import android.net.Uri
import com.application.echo.api.profile.CreateProfileRequest
import com.application.echo.api.profile.CreateProfileResponse
import com.application.echo.api.profile.GetProfileResponse
import com.application.echo.features.profile.model.ProfileResult

interface ProfileNetworkSource {

    suspend fun getProfile(userId: String): ProfileResult<GetProfileResponse>

    suspend fun createProfile(
        userId: String,
        displayName: String,
        firstName: String,
        lastName: String,
        avatarUrl: String,
        fcmToken: String? = null,
    ): ProfileResult<CreateProfileResponse>

    suspend fun uploadAvatar(uri: Uri): ProfileResult<String>
}