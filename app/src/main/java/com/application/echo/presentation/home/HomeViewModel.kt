package com.application.echo.presentation.home

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
) : BaseViewModel<HomeState, HomeEvent, HomeAction>(
    initialState = savedStateHandle[KEY] ?: HomeState()
) {

    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnLogout -> {
                authRepository.logout()
            }
        }
    }

    companion object {
        const val KEY = "HomeViewModel"
    }
}

sealed interface HomeEvent {
}

sealed interface HomeAction {
    data object OnLogout : HomeAction
}


@Parcelize
data class HomeState(
    val temp: String = "This is the Home Screen State"
) : Parcelable
