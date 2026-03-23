package com.application.echo.rootnav

import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.feature.auth.model.AuthState
import com.application.echo.feature.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RootNavViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel<RootNavState, RootNavEvent, RootNavAction>(
    initialState = RootNavState(),
) {

    init {
        authRepository.authStateFlow
            .onEach { authState ->
                sendEvent(RootNavEvent.OnAuthStateChanged(authState))
                sendAction(RootNavAction.UpdateAuthState(authState))
            }
            .launchIn(viewModelScope)
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
