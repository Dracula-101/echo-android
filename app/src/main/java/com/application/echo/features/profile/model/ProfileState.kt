package com.application.echo.features.profile.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileState(
    val id: String,
    val userId: String,
    val email: String? = null,
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val avatarUrl: String? = null,
) : Parcelable {

    companion object {
        val Empty = ProfileState(
            id = "",
            userId = "",
        )
    }
}