package com.application.echo.feature.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserState(

    @SerialName("userId")
    val userId: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("displayName")
    val displayName: String? = null,
) {
    companion object {
        val Empty = UserState()
    }
}