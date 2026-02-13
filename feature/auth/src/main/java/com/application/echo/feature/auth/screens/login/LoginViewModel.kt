package com.application.echo.feature.auth.screens.login

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.application.echo.core.common.platform.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
): BaseViewModel<LoginState, LoginEvent, LoginAction>(
    initialState = savedStateHandle[KEY_STATE] ?: LoginState(email = "", password = "")
) {
    override fun handleAction(action: LoginAction) {

    }

    companion object {
        private const val KEY_STATE = "state"
    }
}

@Parcelize
data class LoginState(
    val email: String = "",
    val password: String = ""
) : Parcelable


sealed class LoginEvent {
    data class OnEmailChange(val email: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    object OnLoginClick : LoginEvent()
}

sealed class LoginAction {
}