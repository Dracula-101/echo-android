package com.application.echo.api.profile

import com.application.echo.api.common.ApiConstants
import com.application.echo.api.common.HealthResponse
import com.application.echo.core.network.model.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit service definition for the User API.
 *
 * Internal — consumers use [ProfileApiRepository] instead.
 */
internal interface ProfileApiService {

    @GET(ApiConstants.USERS_PROFILE_BY_ID)
    suspend fun getProfile(
        @Path("user_id") userId: String,
    ): NetworkResponse<GetProfileResponse>

    @POST(ApiConstants.USERS_PROFILE)
    suspend fun createProfile(
        @Body request: CreateProfileRequest,
    ): NetworkResponse<CreateProfileResponse>

    @GET(ApiConstants.USERS_HEALTH)
    suspend fun health(): NetworkResponse<HealthResponse>
}
