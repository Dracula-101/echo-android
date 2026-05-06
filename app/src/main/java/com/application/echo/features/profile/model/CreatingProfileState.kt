package com.application.echo.features.profile.model

sealed class CreatingProfileState {

    data object Started : CreatingProfileState()

    data class Creating(
        val preRegistrationInfo: PreRegistrationInfo,
        val state: ProfileState,
    ) : CreatingProfileState()

    data object Completed : CreatingProfileState()
}