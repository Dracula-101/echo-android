package com.application.echo.api.profile

import com.application.echo.core.network.result.ApiResult
import com.application.echo.core.network.result.toApiResult
import javax.inject.Inject

/**
 * Default [ProfileApiRepository] backed by [ProfileApiService].
 */
internal class ProfileApiRepositoryImpl @Inject constructor(
    private val api: ProfileApiService,
) : ProfileApiRepository {

    override suspend fun checkUsernameAvailability(username: String): ApiResult<CheckUsernameResponse> = api.checkUsernameAvailability(
        username = username,
    ).toApiResult()

    override suspend fun getProfile(
        userId: String,
    ): ApiResult<GetProfileResponse> = api.getProfile(
        userId = userId,
    ).toApiResult()

    override suspend fun searchUsers(
        query: String,
        limit: Int,
        offset: Int,
    ): ApiResult<SearchProfileResponse> = api.searchUsers(
        query = query,
        limit = limit,
        offset = offset,
    ).toApiResult()

    override suspend fun createProfile(
        userId: String,
        displayName: String,
        userName: String,
        firstName: String,
        lastName: String,
        bio: String,
        profileVisibility: String,
        searchable: Boolean,
        pushEnabled: Boolean,
    ): ApiResult<CreateProfileResponse> = api.createProfile(
        request = CreateProfileRequest(
            userId = userId,
            displayName = displayName,
            username = userName,
            firstName = firstName,
            lastName = lastName,
            bio = bio,
            profileVisibility = profileVisibility,
            searchable = searchable,
            pushEnabled = pushEnabled,
        )
    ).toApiResult()
}
