package com.application.echo.features.profile.model

enum class ProfileVisibility {
    PUBLIC, PRIVATE, FRIENDS_ONLY;

    override fun toString(): String {
        return when (this) {
            PUBLIC -> "public"
            PRIVATE -> "private"
            FRIENDS_ONLY -> "friends"
        }
    }
}