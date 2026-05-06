package com.application.echo.features.profile.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileState(
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val gender: String? = null,
    val dateOfBirth: Long? = null,
    val bio: String? = null,
    val interests: List<String> = emptyList(),
    val canFindByPhoneNumber: Boolean = false,
    val canFindByUsername: Boolean = false,
) : Parcelable {

    companion object {
        val Empty = ProfileState()
    }
}