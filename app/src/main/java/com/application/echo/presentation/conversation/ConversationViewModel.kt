package com.application.echo.presentation.conversation

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.messaging.model.Conversation
import com.application.echo.features.messaging.repository.MessagingRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val messagingRepository: MessagingRepository,
) : BaseViewModel<ConversationState, ConversationEvent, ConversationAction>(
    initialState = savedStateHandle[KEY] ?: ConversationState()
) {

    init {
        val userId = authRepository.userStateFlow.value.userId
        setState { state.copy(currentUserId = userId) }
        observeConversations()
        refreshConversations()
    }

    override fun handleAction(action: ConversationAction) {
        when (action) {
            is ConversationAction.OnLogout -> authRepository.logout()
            is ConversationAction.OnRefresh -> refreshConversations(isRefresh = true)
            is ConversationAction.OnRetry -> refreshConversations()
            is ConversationAction.OnConversationClicked -> {}
            is ConversationAction.OnNewConversationClicked -> {}
        }
    }

    private fun observeConversations() {
        messagingRepository.conversations
            .onEach { conversations ->
                val uiModels = conversations.map { it.toUiModel(state.currentUserId) }
                setState {
                    state.copy(
                        conversations = uiModels,
                        totalUnreadCount = conversations.sumOf { it.unreadCount },
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null,
                    )
                }
                savedStateHandle[KEY] = state
            }
            .catch { e ->
                Timber.e(e, "Error observing conversations")
                setState {
                    state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = "Failed to load conversations",
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshConversations(isRefresh: Boolean = false) {
        viewModelScope.launch {
            setState {
                state.copy(
                    isLoading = !isRefresh && state.conversations.isEmpty(),
                    isRefreshing = isRefresh,
                    errorMessage = null,
                )
            }
            try {
                messagingRepository.refreshConversations()
            } catch (e: Exception) {
                Timber.e(e, "Failed to refresh conversations")
                setState {
                    state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = if (state.conversations.isEmpty()) "Failed to load conversations" else null,
                    )
                }
                sendEvent(
                    ConversationEvent.ShowSnackbar(
                        message = "Failed to load conversations",
                        type = EchoSnackbarType.ERROR,
                    )
                )
            }
        }
    }

    private fun Conversation.toUiModel(currentUserId: String): ConversationItemUiModel {
        val otherParticipant = participants.firstOrNull { it.userId != currentUserId }
        return ConversationItemUiModel(
            conversationId = id,
            participantName = otherParticipant?.displayName ?: "Unknown",
            participantAvatarUrl = otherParticipant?.avatarUrl,
            lastMessageContent = lastMessage?.content,
            formattedTimestamp = formatTimestamp(lastMessage?.timestamp ?: updatedAt),
            onlineStatus = otherParticipant?.onlineStatus?.name?.lowercase(),
            isLastMessageFromMe = lastMessage?.senderId == currentUserId,
            conversationType = conversationType,
            unreadCount = unreadCount,
        )
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
    val totalUnreadCount: Int = 0,
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
    val unreadCount: Int = 0,
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
    if (isoString.isNullOrBlank()) return null
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
