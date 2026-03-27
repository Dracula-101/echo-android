package com.application.echo.features.profile.datasource.network

import com.application.echo.core.api.profile.CreateProfileRequest
import com.application.echo.core.api.profile.CreateProfileResponse
import com.application.echo.core.api.profile.GetProfileResponse
import com.application.echo.core.api.profile.ProfileApiRepository
import com.application.echo.features.profile.model.ProfileResult
import com.application.echo.features.profile.model.toProfileResult
import javax.inject.Inject

class ProfileNetworkSourceImpl @Inject constructor(
    private val profileApi: ProfileApiRepository
) : ProfileNetworkSource {

    override suspend fun getProfile(userId: String): ProfileResult<GetProfileResponse> {
        return profileApi
            .getProfile(userId)
            .toProfileResult()
    }

    override suspend fun createProfile(
        userId: String,
        displayName: String,
        firstName: String,
        lastName: String,
        avatarUrl: String,
        fcmToken: String,
    ): ProfileResult<CreateProfileResponse> {
        return profileApi
            .createProfile(
                userId = userId,
                displayName = displayName,
                firstName = firstName,
                lastName = lastName,
                avatarUrl = avatarUrl,
                fcmToken = fcmToken,
            )
            .toProfileResult()
    }
}