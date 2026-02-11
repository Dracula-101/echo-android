package com.application.echo.core.api.media

import com.application.echo.core.network.result.ApiResult
import java.io.File

/**
 * Public contract for media upload operations.
 */
interface MediaRepository {

    /**
     * Upload a generic media file.
     *
     * @param file The local file to upload.
     * @param mimeType MIME type (e.g. `"image/jpeg"`, `"video/mp4"`).
     */
    suspend fun uploadMedia(
        file: File,
        mimeType: String,
    ): ApiResult<MediaUploadResponse>

    /**
     * Upload a profile photo.
     *
     * @param file The local image file.
     * @param mimeType MIME type (e.g. `"image/jpeg"`, `"image/png"`).
     */
    suspend fun uploadProfilePhoto(
        file: File,
        mimeType: String,
    ): ApiResult<MediaUploadResponse>
}
