package com.application.echo.presentation.create_profile

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.onFailure
import android.net.Uri
import android.os.Parcelable
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import com.application.echo.core.api.manager.AuthTokenManager
import com.application.echo.core.api.profile.ProfileError
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.core.common.repository.model.DataState
import com.application.echo.features.notification.token.FcmTokenManager
import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.model.fold
import com.application.echo.features.profile.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ProfileRepository,
    private val fcmTokenManager: FcmTokenManager,
    private val authTokenManager: AuthTokenManager,
) : BaseViewModel<CreateProfileState, CreateProfileEvent, CreateProfileAction>(
    initialState = savedStateHandle[KEY_STATE] ?: CreateProfileState(userId = ""),
) {

    init {
        repository.creatingProfileStateFlow
            .onEach { profileState ->
                if (profileState is CreatingProfileState.Creating) {
                    setState {
                        state.copy(
                            userId = profileState.userId,
                            displayName = profileState.displayName,
                            firstName = profileState.firstName,
                            lastName = profileState.lastName,
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

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
                state.copy(avatarUri = action.uri)
            }
            CreateProfileAction.OnSaveClicked -> saveProfile()
        }
        savedStateHandle[KEY_STATE] = state
    }

    private fun saveProfile() {
        viewModelScope.launch {
            Timber.d("Saving profile, token: ${authTokenManager.getLatestAuthTokenData()}")
            setState { state.copy(isLoading = true) }
            if (state.avatarUri != null) {
                val result = repository.setProfileInfo(
                    userId = state.userId,
                    displayName = state.displayName.orEmpty(),
                    firstName = state.firstName.orEmpty(),
                    lastName = state.lastName.orEmpty(),
                    avatarUri = state.avatarUri!!,
                )
                result.fold(
                    onSuccess = {

                    },
                    onError = {
                        sendEvent(CreateProfileEvent.ShowSnackbar("Failed to create profile", it.message, it.code,))
                    }
                )
            } else {
                sendEvent(CreateProfileEvent.ShowSnackbar("Select an avatar", "No avatar selected for profile", "NO_AVATAR"))
            }
        }.invokeOnCompletion {
            setState { state.copy(isLoading = false) }
        }
    }

    companion object {
        private const val KEY_STATE = "create_profile_state"
    }
}

@Parcelize
data class CreateProfileState(
    val userId: String,
    val displayName: String? = "Dracula",
    val firstName: String? = "Pratik",
    val lastName: String? = "Pujari",
    val avatarUri: Uri? = null,
    val avatarUrl: String? = null,
    val isLoading: Boolean = false,
) : Parcelable

sealed interface CreateProfileEvent {
    data class ShowSnackbar(val message: String, val detail: String, val code: String): CreateProfileEvent
}

sealed interface CreateProfileAction {
    data class OnDisplayNameChanged(val value: String) : CreateProfileAction
    data class OnFirstNameChanged(val value: String) : CreateProfileAction
    data class OnLastNameChanged(val value: String) : CreateProfileAction
    data class OnAvatarSelected(val uri: Uri) : CreateProfileAction
    data object OnSaveClicked : CreateProfileAction
}