package com.application.echo.presentation.rootnav

import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
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

        resolved
            .onEach { authState ->
                if (state.startRoute == null) {
                    setState { state.copy(startRoute = authState.toStartRoute()) }
                }
            }
            .launchIn(viewModelScope)

        resolved
            .drop(1)
            .distinctUntilChanged { old, new -> old::class == new::class }
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
    is AuthState.Authenticated   -> ConversationScreenRoute
    is AuthState.RegisteringWithPhone -> RegisterScreenRoute
    is AuthState.CreateProfile   -> CreateProfileScreenRoute(userId = this.userId)
    is AuthState.OtpVerification -> OtpScreenRoute
    is AuthState.Unauthenticated -> LoginScreenRoute
    is AuthState.Initializing    -> error("Initializing is filtered out before reaching toStartRoute")
}