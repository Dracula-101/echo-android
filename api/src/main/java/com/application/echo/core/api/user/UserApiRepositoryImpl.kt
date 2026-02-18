package com.application.echo.core.api.user

import com.application.echo.core.network.result.ApiResult
import com.application.echo.core.network.result.toApiResult
import javax.inject.Inject

/**
 * Default [UserApiRepository] backed by [UserApiService].
 */
internal class UserApiRepositoryImpl @Inject constructor(
    private val api: UserApiService,
) : UserApiRepository {

    override suspend fun getProfile(
        userId: String,
    ): ApiResult<UserProfileResponse> = api.getProfile(
        userId = userId,
    ).toApiResult()

    override suspend fun createProfile(
        userId: String,
        displayName: String,
        firstName: String,
        lastName: String,
        avatarUrl: String?,
        fcmToken: String?,
    ): ApiResult<UserProfileResponse> = api.createProfile(
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
