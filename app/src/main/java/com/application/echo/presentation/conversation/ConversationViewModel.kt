package com.application.echo.presentation.conversation

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.api.message.MessageApiRepository
import com.application.echo.api.message.ConversationResponse
import com.application.echo.api.profile.GetProfileResponse
import com.application.echo.api.profile.ProfileApiRepository
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.core.network.model.NetworkException
import com.application.echo.core.network.result.fold
import com.application.echo.core.network.result.getOrNull
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val messageApiRepository: MessageApiRepository,
    private val profileApiRepository: ProfileApiRepository,
) : BaseViewModel<ConversationState, ConversationEvent, ConversationAction>(
    initialState = savedStateHandle[KEY] ?: ConversationState()
) {

    private val profileCache = ConcurrentHashMap<String, GetProfileResponse>()

    init {
        val userId = authRepository.userStateFlow.value.userId
        setState { state.copy(currentUserId = userId) }
        loadConversations()
    }

    override fun handleAction(action: ConversationAction) {
        when (action) {
            is ConversationAction.OnLogout -> authRepository.logout()
            is ConversationAction.OnRefresh -> loadConversations(isRefresh = true)
            is ConversationAction.OnRetry -> loadConversations()
            is ConversationAction.OnConversationClicked -> {
                // Future: navigate to chat detail screen
            }
            is ConversationAction.OnNewConversationClicked -> {
                // Future: navigate to new conversation screen
            }
        }
    }

    private fun loadConversations(isRefresh: Boolean = false) {
        viewModelScope.launch {
            setState {
                state.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    errorMessage = null,
                )
            }

            messageApiRepository.getMyConversations().fold(
                onSuccess = { conversations ->
                    val uiModels = buildAllUiModels(conversations)
                    setState {
                        state.copy(
                            conversations = uiModels,
                            isLoading = false,
                            isRefreshing = false,
                        )
                    }
                    savedStateHandle[KEY] = state
                },
                onFailure = { exception ->
                    val message = mapExceptionToMessage(exception)
                    Timber.e("Failed to load conversations: %s", message)
                    setState {
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = message,
                        )
                    }
                    sendEvent(
                        ConversationEvent.ShowSnackbar(
                            message = "Failed to load conversations",
                            detail = message,
                            type = EchoSnackbarType.ERROR,
                        )
                    )
                    savedStateHandle[KEY] = state
                },
            )
        }
    }

    private suspend fun buildAllUiModels(
        conversations: List<ConversationResponse>,
    ): List<ConversationItemUiModel> = coroutineScope {
        conversations.map { conversation ->
            async { buildConversationUiModel(conversation) }
        }.awaitAll()
    }

    private fun buildConversationUiModel(
        conversation: ConversationResponse,
    ): ConversationItemUiModel {
        val conversationParticipant = conversation.participants?.firstOrNull {
            it.userId != state.currentUserId
        } ?: throw IllegalStateException("Conversation has no participants")
        return ConversationItemUiModel(
            conversationId = conversation.id,
            participantName = conversationParticipant.displayName,
            participantAvatarUrl = conversationParticipant.avatarUrl,
            lastMessageContent = conversation.lastMessage?.content,
            formattedTimestamp = formatTimestamp(
                conversation.lastMessage?.createdAt ?: conversation.updatedAt
            ),
            onlineStatus = conversationParticipant.onlineStatus,
            isLastMessageFromMe = conversation.lastMessage?.senderId == state.currentUserId,
            conversationType = conversation.conversationType,
        )
    }

    private fun mapExceptionToMessage(exception: NetworkException): String = when (exception) {
        is NetworkException.Http -> exception.message
        is NetworkException.Network -> "No internet connection"
        is NetworkException.Timeout -> "Request timed out"
        is NetworkException.Serialization -> "Unexpected response format"
        is NetworkException.Unknown -> "Something went wrong"
    }

    companion object {
        private const val KEY = "ConversationViewModel"
    }
}

// ── State ────────────────────────────────────────────────────────────

@Parcelize
data class ConversationState(
    val conversations: List<ConversationItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val currentUserId: String = "",
) : Parcelable

@Parcelize
data class ConversationItemUiModel(
    val conversationId: String,
    val participantName: String,
    val participantAvatarUrl: String?,
    val onlineStatus: String? = null,
    val lastMessageContent: String?,
    val formattedTimestamp: String?,
    val isLastMessageFromMe: Boolean,
    val conversationType: String?,
) : Parcelable

// ── Events ───────────────────────────────────────────────────────────

sealed interface ConversationEvent {
    data class ShowSnackbar(
        val message: String,
        val detail: String? = null,
        val code: String? = null,
        val type: EchoSnackbarType = EchoSnackbarType.ERROR,
    ) : ConversationEvent
}

// ── Actions ──────────────────────────────────────────────────────────

sealed interface ConversationAction {
    data object OnLogout : ConversationAction
    data object OnRefresh : ConversationAction
    data object OnRetry : ConversationAction
    data class OnConversationClicked(val conversationId: String) : ConversationAction
    data object OnNewConversationClicked : ConversationAction
}

// ── Timestamp formatting ─────────────────────────────────────────────

private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
    isLenient = true
}
private val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
private val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.US)
private val monthDayFormat = SimpleDateFormat("MMM d", Locale.US)
private val fullDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

private fun formatTimestamp(isoString: String?): String? {
    if (isoString == null) return null
    return try {
        val cleaned = isoString
            .replace("Z", "+0000")
            .replace(Regex("([+-]\\d{2}):(\\d{2})$"), "$1$2")

        val date = isoFormat.parse(cleaned) ?: return null
        val now = Calendar.getInstance()
        val then = Calendar.getInstance().apply { time = date }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val daysDiff = ((today.timeInMillis - then.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        when {
            daysDiff <= 0 -> timeFormat.format(date)
            daysDiff == 1 -> "Yesterday"
            daysDiff < 7 -> dayOfWeekFormat.format(date)
            now.get(Calendar.YEAR) == then.get(Calendar.YEAR) -> monthDayFormat.format(date)
            else -> fullDateFormat.format(date)
        }
    } catch (e: Exception) {
        Timber.w(e, "Failed to parse timestamp: %s", isoString)
        null
    }
}
