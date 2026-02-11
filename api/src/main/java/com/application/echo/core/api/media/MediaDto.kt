package com.application.echo.core.api.media

import com.google.gson.annotations.SerializedName

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Response Bodies
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Response `data` for `POST /media/upload` and `POST /media/profile-photo`.
 */
data class MediaUploadResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("file_name")
    val fileName: String? = null,
    @SerializedName("content_type")
    val contentType: String? = null,
    @SerializedName("size")
    val size: Long? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
)
