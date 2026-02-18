package com.application.echo.core.api.user

import com.application.echo.core.network.result.ApiResult

/**
 * Public contract for all user-profile operations.
 */
interface UserApiRepository {

    /**
     * Fetch a user's profile by their ID.
     */
    suspend fun getProfile(userId: String): ApiResult<UserProfileResponse>

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
    ): ApiResult<UserProfileResponse>
}
