package com.application.echo.presentation.create_profile

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.onFailure
import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.profile.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ProfileRepository,
) : BaseViewModel<CreateProfileState, CreateProfileEvent, CreateProfileAction>(
    initialState = savedStateHandle[KEY_STATE] ?: CreateProfileState(),
) {

    override fun handleAction(action: CreateProfileAction) {
        when (action) {
            is CreateProfileAction.OnDisplayNameChanged -> setState {
                state.copy(displayName = action.value)
            }
            is CreateProfileAction.OnFirstNameChanged -> setState {
                state.copy(firstName = action.value)
            }
            is CreateProfileAction.OnLastNameChanged -> setState {
                state.copy(lastName = action.value)
            }
            is CreateProfileAction.OnAvatarSelected -> setState {
                state.copy(avatarUrl = action.uri.toString())
            }
            CreateProfileAction.OnSaveClicked -> saveProfile()
        }
        savedStateHandle[KEY_STATE] = state
    }

    private fun saveProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
            }.onSuccess {
                sendEvent(CreateProfileEvent.NavigateToHome)
            }.onFailure { error ->
                sendEvent(CreateProfileEvent.ShowError(error.message ?: "Something went wrong"))
            }
        }
    }

    companion object {
        private const val KEY_STATE = "create_profile_state"
    }
}

@Parcelize
data class CreateProfileState(
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val avatarUrl: String? = null,
    val isLoading: Boolean = false,
) : Parcelable

sealed interface CreateProfileEvent {
    data object NavigateToHome : CreateProfileEvent
    data class ShowError(val message: String) : CreateProfileEvent
}

sealed interface CreateProfileAction {
    data class OnDisplayNameChanged(val value: String) : CreateProfileAction
    data class OnFirstNameChanged(val value: String) : CreateProfileAction
    data class OnLastNameChanged(val value: String) : CreateProfileAction
    data class OnAvatarSelected(val uri: Uri) : CreateProfileAction
    data object OnSaveClicked : CreateProfileAction
}