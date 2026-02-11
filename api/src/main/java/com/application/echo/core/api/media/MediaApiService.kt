package com.application.echo.core.api.media

import com.application.echo.core.api.common.ApiConstants
import com.application.echo.core.api.common.HealthResponse
import com.application.echo.core.network.model.NetworkResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Retrofit service definition for the Media API.
 *
 * Internal — consumers use [MediaRepository] instead.
 */
internal interface MediaApiService {

    @Multipart
    @POST(ApiConstants.MEDIA_UPLOAD)
    suspend fun uploadMedia(
        @Part file: MultipartBody.Part,
    ): NetworkResponse<MediaUploadResponse>

    @Multipart
    @POST(ApiConstants.MEDIA_PROFILE_PHOTO)
    suspend fun uploadProfilePhoto(
        @Part file: MultipartBody.Part,
    ): NetworkResponse<MediaUploadResponse>

    @GET(ApiConstants.MEDIA_HEALTH)
    suspend fun health(): NetworkResponse<HealthResponse>
}
