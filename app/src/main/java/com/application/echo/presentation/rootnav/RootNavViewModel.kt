package com.application.echo.presentation.rootnav

import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RootNavViewModel @Inject constructor(
    authRepository: AuthRepository,
) : BaseViewModel<RootNavState, RootNavEvent, RootNavAction>(
    initialState = RootNavState(),
) {

    init {
        val resolved = authRepository.authStateFlow
            .filter { it !is AuthState.Initializing }

        // First resolved state seeds the start destination — no navigation event,
        // so we don't pay a NavHost transition on cold start.
        resolved
            .onEach { authState ->
                if (state.startRoute == null) {
                    setState { state.copy(startRoute = authState.toStartRoute()) }
                }
            }
            .launchIn(viewModelScope)

        // Subsequent changes (logout, session expiry, etc.) drive runtime navigation.
        resolved
            .drop(1)
            .onEach { sendEvent(RootNavEvent.OnAuthStateChanged(it)) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: RootNavAction) = Unit
}

sealed class RootNavEvent {
    data class OnAuthStateChanged(val authState: AuthState) : RootNavEvent()
}

sealed class RootNavAction

data class RootNavState(
    val startRoute: Any? = null,
)

private fun AuthState.toStartRoute(): Any = when (this) {
    is AuthState.Authenticated -> ConversationScreenRoute
    is AuthState.CreateProfile -> CreateProfileRoute
    is AuthState.OtpVerification -> OtpScreenRoute
    is AuthState.Unauthenticated -> LoginScreenRoute
    is AuthState.Initializing -> error("Initializing is filtered out before reaching toStartRoute")
}
