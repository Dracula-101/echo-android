package com.application.echo.core.api.media

import com.google.gson.annotations.SerializedName

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Response Bodies
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Response `data` for `POST /media/upload` and `POST /media/profile-photo`.
 */
data class MediaUploadResponse(
    @SerializedName("file_id")
    val fileId: String,
    @SerializedName("file_name")
    val fileName: String,
    @SerializedName("file_size")
    val fileSize: Int,
    @SerializedName("processing_status")
    val processingStatus: String,
    @SerializedName("storage_url")
    val storageUrl: String,
    @SerializedName("uploaded_at")
    val uploadedAt: String
)
