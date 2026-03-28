package com.application.echo.features.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserState(
    val userId: String,
    val email: String? = null,
    val displayName: String? = null,
) {
    companion object {
        val Empty = UserState(email = null, displayName = null, userId = "")
    }
}