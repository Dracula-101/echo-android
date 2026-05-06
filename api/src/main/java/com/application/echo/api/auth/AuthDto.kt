package com.application.echo.api.auth

import com.google.gson.annotations.SerializedName

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//  Request Bodies
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Request body for `POST /auth/login`.
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("verify_token")
    val verifyToken: String? = null,
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
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("phone_country_code")
    val countryCode: String? = null,
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
    @SerializedName("account_status")
    val accountStatus: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("email_verified")
    val emailVerified: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("next_step")
    val nextStep: String,
    @SerializedName("phone_verified")
    val phoneVerified: Boolean,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("verification_email_sent_to")
    val verificationEmailSentTo: String,
)

/**
 * Response `data` for `POST /auth/refresh-token`.
 */
data class RefreshTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_at")
    val expiresAt: String,
)

data class RefreshTokenWrapper(
    @SerializedName("data")
    val data: RefreshTokenResponse
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
    @SerializedName("expires_at")
    val expiresAt: String,
)
