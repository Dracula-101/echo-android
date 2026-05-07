package com.application.echo.features.profile.model

enum class ProfileCreationState {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED;

    override fun toString(): String {
        return name
    }

    companion object {
        fun parse(state: String?): ProfileCreationState {
            return when (state) {
                IN_PROGRESS.name -> IN_PROGRESS
                COMPLETED.name -> COMPLETED
                else -> NOT_STARTED
            }
        }
    }
}