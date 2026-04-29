package com.application.echo.presentation.register

import android.os.Parcelable
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.api.auth.AuthError
import com.application.echo.features.auth.model.onError
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
) : BaseViewModel<RegisterState, RegisterEvent, RegisterAction>(
    initialState = savedStateHandle[KEY_STATE] ?: RegisterState(),
) {

    override fun handleAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnPhoneNumberChanged -> {
                setState {
                    state.copy(phoneNumber = action.phoneNumber)
                }
            }
        }
    }

    companion object {
        private const val KEY_STATE = "register_state"
    }
}

@Parcelize
data class RegisterState(
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val countryIsoCode: String = "",
    val isLoading: Boolean = false,
) : Parcelable {
}

sealed interface RegisterEvent {
    data class ShowSnackbar(
        val message: String,
        val detail: String? = null,
        val code: String? = null,
        val type: EchoSnackbarType
    ) : RegisterEvent
}

sealed interface RegisterAction {
    data class OnPhoneNumberChanged(val phoneNumber: String) : RegisterAction
}
