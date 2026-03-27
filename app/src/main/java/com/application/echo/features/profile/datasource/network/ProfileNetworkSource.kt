package com.application.echo.features.profile.datasource.network

import com.application.echo.core.api.profile.CreateProfileRequest
import com.application.echo.core.api.profile.CreateProfileResponse
import com.application.echo.core.api.profile.GetProfileResponse
import com.application.echo.features.profile.model.ProfileResult

interface ProfileNetworkSource {

    suspend fun getProfile(userId: String): ProfileResult<GetProfileResponse>

    suspend fun createProfile(
        userId: String,
        displayName: String,
        firstName: String,
        lastName: String,
        avatarUrl: String,
        fcmToken: String,
    ): ProfileResult<CreateProfileResponse>
}