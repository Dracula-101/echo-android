package com.application.echo.api.common

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
    const val USERS_CHECK_USERNAME = "$API_PREFIX/users/username-exists"
    const val USERS_PROFILE = "$API_PREFIX/users/me"
    const val USERS_PROFILE_BY_ID = "$API_PREFIX/users/profile/{user_id}"
    const val USERS_SEARCH = "$API_PREFIX/users/search"
    const val USERS_HEALTH = "$API_PREFIX/users/health"

    // ── Media ──
    const val MEDIA_UPLOAD = "$API_PREFIX/media/upload"
    const val MEDIA_PROFILE_PHOTO = "$API_PREFIX/media/profile-photo"
    const val MEDIA_HEALTH = "$API_PREFIX/media/health"

    // ── Messages ──
    const val MESSAGES = "$API_PREFIX/messages"
    const val MESSAGES_HEALTH = "$API_PREFIX/messages/health"
    const val MESSAGES_SYNC = "$API_PREFIX/messages/sync"
    const val MESSAGE_BY_ID = "$API_PREFIX/messages/{message_id}"
    const val MESSAGE_READ = "$API_PREFIX/messages/{message_id}/read"
    const val CONVERSATIONS = "$API_PREFIX/messages/conversations"
    const val CONVERSATION_BY_ID = "$API_PREFIX/messages/conversations/{conversation_id}"
    const val MY_CONVERSATIONS = "$API_PREFIX/messages/conversations/me"

    // ── Headers ──
    const val HEADER_SESSION_ID = "X-Session-ID"
    const val HEADER_SESSION_TOKEN = "X-Session-Token"
}
