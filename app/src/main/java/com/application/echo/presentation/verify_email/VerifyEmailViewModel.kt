package com.application.echo.presentation.verify_email

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.presentation.rootnav.VerifyEmailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<VerifyEmailState, VerifyEmailEvent, VerifyEmailAction>(
    initialState = savedStateHandle[KEY_STATE] ?: VerifyEmailState(
        token = savedStateHandle.toRoute<VerifyEmailRoute>().token
    ),
) {

    override fun handleAction(action: VerifyEmailAction) {
        savedStateHandle[KEY_STATE] = state
    }

    companion object {
        private const val KEY_STATE = "Verify_email"
    }
}

@Parcelize
data class VerifyEmailState(
    val isLoading: Boolean = false,
    val token: String,
) : Parcelable

sealed interface VerifyEmailEvent

sealed interface VerifyEmailAction