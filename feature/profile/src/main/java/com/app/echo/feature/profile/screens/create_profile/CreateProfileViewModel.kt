package com.app.echo.feature.profile.screens.create_profile

import androidx.lifecycle.SavedStateHandle
import com.application.echo.core.common.platform.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.Serializable
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
): BaseViewModel<CreateProfileState, CreateProfileEvent, CreateProfileAction>(
    initialState = savedStateHandle[KEY_STATE] ?: CreateProfileState(),
) {

    override fun handleAction(action: CreateProfileAction) {

    }

    companion object {
        const val KEY_STATE = "create_profile_state"
    }
}

sealed interface CreateProfileEvent {

    data class OnNameChanged(val name: String): CreateProfileEvent

    data class On
}

sealed interface CreateProfileAction {

}

@Serializable
data class CreateProfileState(
    val userId: String? = null,
    val displayName: String = "",
    val bio: String = "",
    val profilePictureUrl: String? = null,
    val nameError : String? = null,
)