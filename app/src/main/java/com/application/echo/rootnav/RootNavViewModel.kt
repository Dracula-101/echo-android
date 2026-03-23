package com.application.echo.rootnav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.core.navigation.Navigator
import com.application.echo.feature.auth.model.UserState
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
        authRepository.userStateFlow
            .onEach { userState ->
                sendEvent(RootNavEvent.AuthStateChanged(userState))
            }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: RootNavAction) {
    }

}

sealed class RootNavEvent {

    data class AuthStateChanged(val userState: UserState) : RootNavEvent()

}

sealed class RootNavAction {

}

data class RootNavState(
    val userLoggedIn: Boolean = false,
)