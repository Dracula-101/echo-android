package com.application.echo.api.profile

import com.application.echo.core.network.result.ApiResult

/**
 * Public contract for all user-profile operations.
 */
interface ProfileApiRepository {

    /**
     * Fetch a user's profile by their ID.
     */
    suspend fun getProfile(userId: String): ApiResult<GetProfileResponse>

    /**
     * Search users by username or display name.
     */
    suspend fun searchUsers(
        query: String,
        limit: Int = 20,
        offset: Int = 0,
    ): ApiResult<SearchProfileResponse>

    /**
     * Create (or update) a user profile.
     */
    suspend fun createProfile(
        userId: String,
        displayName: String,
        firstName: String,
        lastName: String,
        avatarUrl: String? = null,
        fcmToken: String? = null,
    ): ApiResult<CreateProfileResponse>
}
