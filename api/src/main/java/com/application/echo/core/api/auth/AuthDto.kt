package com.application.echo.core.api.auth

import com.google.gson.annotations.SerializedName

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Request Bodies
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Request body for `POST /auth/login`.
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("fcm_token")
    val fcmToken: String? = null,
    @SerializedName("apns_token")
    val apnsToken: String? = null,
)

/**
 * Request body for `POST /auth/register`.
 */
data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("accept_terms")
    val acceptTerms: Boolean,
)

/**
 * Request body for `POST /auth/refresh-token`.
 */
data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String,
)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Response Bodies
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Response `data` for `POST /auth/login`.
 *
 * Contains the authenticated [user] and their [session] credentials.
 */
data class LoginResponse(
    @SerializedName("user")
    val user: AuthUser,
    @SerializedName("session")
    val session: SessionInfo,
)

/**
 * Response `data` for `POST /auth/register`.
 */
data class RegisterResponse(
    @SerializedName("user")
    val user: AuthUser,
    @SerializedName("session")
    val session: SessionInfo,
)

/**
 * Response `data` for `POST /auth/refresh-token`.
 */
data class RefreshTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Shared Models
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * User object returned inside auth responses.
 */
data class AuthUser(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
)

/**
 * Session credentials returned after login / register.
 */
data class SessionInfo(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("session_token")
    val sessionToken: String,
)
