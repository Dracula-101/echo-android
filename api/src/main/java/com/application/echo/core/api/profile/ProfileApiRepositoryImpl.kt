package com.application.echo.core.api.profile

import com.application.echo.core.network.result.ApiResult
import com.application.echo.core.network.result.toApiResult
import javax.inject.Inject

/**
 * Default [ProfileApiRepository] backed by [ProfileApiService].
 */
internal class ProfileApiRepositoryImpl @Inject constructor(
    private val api: ProfileApiService,
) : ProfileApiRepository {

    override suspend fun getProfile(
        userId: String,
    ): ApiResult<ProfileResponse> = api.getProfile(
        userId = userId,
    ).toApiResult()

    override suspend fun createProfile(
        userId: String,
        displayName: String,
        firstName: String,
        lastName: String,
        avatarUrl: String?,
        fcmToken: String?,
    ): ApiResult<ProfileResponse> = api.createProfile(
        request = CreateProfileRequest(
            userId = userId,
            displayName = displayName,
            firstName = firstName,
            lastName = lastName,
            avatarUrl = avatarUrl,
            fcmToken = fcmToken,
        ),
    ).toApiResult()
}
