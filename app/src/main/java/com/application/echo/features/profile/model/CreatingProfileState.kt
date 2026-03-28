package com.application.echo.features.profile.model

sealed class CreatingProfileState {

    data object None : CreatingProfileState()

    data class Creating(val userId: String) : CreatingProfileState()

    data object Completed : CreatingProfileState()
}