package com.application.echo.api.profile

import com.google.gson.annotations.SerializedName

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Request Bodies
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Request body for `POST /users/profile`.
 */
data class CreateProfileRequest(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("fcm_token")
    val fcmToken: String? = null,
)


// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Response Bodies
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Response `data` for `GET /users/profile/{user_id}` and `POST /users/profile`.
 */
data class GetProfileResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("display_name")
    val displayName: String? = null,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("fcm_token")
    val fcmToken: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
)


/**
 * Response `data` for `POST /users/profile`. Mirrors `GetProfileResponse` since the API returns the same data for both endpoints.
 */
data class CreateProfileResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("display_name")
    val displayName: String? = null,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("fcm_token")
    val fcmToken: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
)

data class SearchProfileResponse(
    @SerializedName("users")
    val users: List<SearchedUser>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("offset")
    val offset: Int,
)

data class SearchedUser(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
)