package com.application.echo.features.profile.model

sealed class CreatingProfileState {

    data object None : CreatingProfileState()

    data class Creating(
        val userId: String,
        val displayName: String? = null,
        val firstName: String? = null,
        val lastName: String? = null
    ) : CreatingProfileState()

    data object Completed : CreatingProfileState()
}