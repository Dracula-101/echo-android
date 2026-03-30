package com.application.echo.presentation.search_user

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.api.message.ConversationType
import com.application.echo.api.message.MessageApiRepository
import com.application.echo.api.profile.ProfileApiRepository
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.core.network.result.fold
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val profileApiRepository: ProfileApiRepository,
    private val messageApiRepository: MessageApiRepository,
) : BaseViewModel<SearchUserState, SearchUserEvent, SearchUserAction>(
    initialState = savedStateHandle[KEY] ?: SearchUserState()
) {

    private var searchJob: Job? = null
    private val currentUserId = authRepository.userStateFlow.value.userId

    override fun handleAction(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.OnQueryChanged -> {
                setState { state.copy(query = action.query) }
                debounceSearch(action.query)
            }
            is SearchUserAction.OnUserClicked -> {
                createConversation(action.userId, action.displayName)
            }
        }
    }

    private fun debounceSearch(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            setState { state.copy(results = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            setState { state.copy(isSearching = true) }
            profileApiRepository.searchUsers(query).fold(
                onSuccess = { profiles ->
                    val results = profiles
                        .users
                        .filter { it.userId != currentUserId }
                        .map { profile ->
                            SearchResultUiModel(
                                userId = profile.userId,
                                displayName = profile.displayName,
                                avatarUrl = profile.avatarUrl,
                            )
                        }
                    setState { state.copy(results = results, isSearching = false) }
                },
                onFailure = { exception ->
                    Timber.e("Search failed: %s", exception.throwable.message)
                    setState { state.copy(results = emptyList(), isSearching = false) }
                    sendEvent(
                        SearchUserEvent.ShowSnackbar(
                            message = "Search failed",
                            type = EchoSnackbarType.ERROR,
                        )
                    )
                },
            )
        }
    }

    private fun createConversation(userId: String, displayName: String) {
        if (state.isCreating) return
        setState { state.copy(isCreating = true, creatingUserId = userId) }
        viewModelScope.launch {
            messageApiRepository.createConversation(
                conversationType = ConversationType.DIRECT,
                participantIds = listOf(userId),
            ).fold(
                onSuccess = { response ->
                    setState { state.copy(isCreating = false, creatingUserId = null) }
                    sendEvent(SearchUserEvent.NavigateToChat(response.id, displayName))
                },
                onFailure = { exception ->
                    Timber.e("Create conversation failed: %s", exception.throwable.message)
                    setState { state.copy(isCreating = false, creatingUserId = null) }
                    sendEvent(
                        SearchUserEvent.ShowSnackbar(
                            message = "Failed to create conversation",
                            type = EchoSnackbarType.ERROR,
                        )
                    )
                },
            )
        }
    }

    companion object {
        private const val KEY = "SearchUserViewModel"
        private const val DEBOUNCE_MS = 500L
    }
}

// ── State ────────────────────────────────────────────────────────────

@Parcelize
data class SearchUserState(
    val query: String = "",
    val isSearching: Boolean = false,
    val isCreating: Boolean = false,
    val creatingUserId: String? = null,
    val results: List<SearchResultUiModel> = emptyList(),
) : Parcelable

@Parcelize
data class SearchResultUiModel(
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
) : Parcelable

// ── Events ───────────────────────────────────────────────────────────

sealed interface SearchUserEvent {
    data class ShowSnackbar(
        val message: String,
        val type: EchoSnackbarType = EchoSnackbarType.ERROR,
    ) : SearchUserEvent

    data class NavigateToChat(
        val conversationId: String,
        val participantName: String,
    ) : SearchUserEvent
}

// ── Actions ──────────────────────────────────────────────────────────

sealed interface SearchUserAction {
    data class OnQueryChanged(val query: String) : SearchUserAction
    data class OnUserClicked(val userId: String, val displayName: String) : SearchUserAction
}
