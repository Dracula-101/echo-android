package com.application.echo.core.api.media

import com.application.echo.core.network.result.ApiResult
import com.application.echo.core.network.result.toApiResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

/**
 * Default [MediaRepository] backed by [MediaApiService].
 */
internal class MediaRepositoryImpl @Inject constructor(
    private val api: MediaApiService,
) : MediaRepository {

    override suspend fun uploadMedia(
        file: File,
        mimeType: String,
    ): ApiResult<MediaUploadResponse> {
        val part = file.toMultipartPart(mimeType)
        return api.uploadMedia(part).toApiResult()
    }

    override suspend fun uploadProfilePhoto(
        file: File,
        mimeType: String,
    ): ApiResult<MediaUploadResponse> {
        val part = file.toMultipartPart(mimeType)
        return api.uploadProfilePhoto(part).toApiResult()
    }

    // ──────────────── Helpers ────────────────

    private fun File.toMultipartPart(mimeType: String): MultipartBody.Part {
        val requestBody = asRequestBody(mimeType.toMediaType())
        return MultipartBody.Part.createFormData(
            name = PART_NAME,
            filename = name,
            body = requestBody,
        )
    }

    private companion object {
        const val PART_NAME = "file"
    }
}
