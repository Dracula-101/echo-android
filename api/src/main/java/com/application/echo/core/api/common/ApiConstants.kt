package com.application.echo.core.api.common

/**
 * Shared API path constants.
 *
 * All paths are relative to the base URL configured in [HttpClientConfig].
 * The backend is versioned at `api/v1`.
 */
internal object ApiConstants {

    /** API version prefix. */
    const val API_PREFIX = "api/v1"

    // ── Auth ──
    const val AUTH_LOGIN = "$API_PREFIX/auth/login"
    const val AUTH_REGISTER = "$API_PREFIX/auth/register"
    const val AUTH_REFRESH_TOKEN = "$API_PREFIX/auth/refresh-token"
    const val AUTH_HEALTH = "$API_PREFIX/auth/health"

    // ── Users ──
    const val USERS_PROFILE = "$API_PREFIX/users/profile"
    const val USERS_PROFILE_BY_ID = "$API_PREFIX/users/profile/{user_id}"
    const val USERS_HEALTH = "$API_PREFIX/users/health"

    // ── Media ──
    const val MEDIA_UPLOAD = "$API_PREFIX/media/upload"
    const val MEDIA_PROFILE_PHOTO = "$API_PREFIX/media/profile-photo"
    const val MEDIA_HEALTH = "$API_PREFIX/media/health"

    // ── Messages ──
    const val MESSAGES = "$API_PREFIX/messages"
    const val MESSAGES_HEALTH = "$API_PREFIX/messages/health"
    const val CONVERSATIONS = "$API_PREFIX/messages/conversations"
    const val CONVERSATION_BY_ID = "$API_PREFIX/messages/conversations/{conversation_id}"
    const val MY_CONVERSATIONS = "$API_PREFIX/messages/conversations/me"

    // ── Headers ──
    const val HEADER_SESSION_ID = "X-Session-ID"
    const val HEADER_SESSION_TOKEN = "X-Session-Token"
    const val HEADER_DEVICE_NAME = "X-Device-Name"
    const val HEADER_DEVICE_ID = "X-Device-ID"
    const val HEADER_DEVICE_TYPE = "X-Device-Type"
    const val HEADER_DEVICE_PLATFORM = "X-Device-Platform"
    const val HEADER_DEVICE_OS = "X-Device-OS"
    const val HEADER_DEVICE_OS_VERSION = "X-Device-OS-Version"
    const val HEADER_DEVICE_MODEL = "X-Device-Model"
    const val HEADER_DEVICE_MANUFACTURER = "X-Device-Manufacturer"
    const val HEADER_REAL_IP = "X-Real-IP"
}
