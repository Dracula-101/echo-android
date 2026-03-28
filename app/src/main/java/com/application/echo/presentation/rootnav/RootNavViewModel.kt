package com.application.echo.presentation.rootnav

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.profile.model.CreatingProfileState
import com.application.echo.features.profile.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RootNavViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    authRepository: AuthRepository,
    profileRepository: ProfileRepository
) : BaseViewModel<RootNavState, RootNavEvent, RootNavAction>(
    initialState = savedStateHandle[KEY_STATE] ?: RootNavState()
) {

    init {
        authRepository.authStateFlow
            .onEach { authState ->
                sendEvent(RootNavEvent.OnAuthStateChanged(authState))
                sendAction(RootNavAction.UpdateAuthState(authState))
            }.launchIn(viewModelScope)
    }

    override fun handleAction(action: RootNavAction) {
        when (action) {
            is RootNavAction.UpdateAuthState -> {
                setState {
                    state.copy(
                        isLoading = action.authState is AuthState.Initializing,
                        userLoggedIn = action.authState is AuthState.Authenticated,
                    )
                }
            }
        }
    }

    companion object {
        private const val KEY_STATE = "root_nav_state"
    }
}

sealed class RootNavEvent {
    data class OnAuthStateChanged(val authState: AuthState) : RootNavEvent()
}

sealed class RootNavAction {
    data class UpdateAuthState(val authState: AuthState) : RootNavAction()
}

data class RootNavState(
    val isLoading: Boolean = true,
    val userLoggedIn: Boolean = false,
)
