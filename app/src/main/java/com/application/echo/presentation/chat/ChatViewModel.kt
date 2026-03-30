package com.application.echo.presentation.chat

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.api.message.MessageApiRepository
import com.application.echo.api.message.MessageType
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.core.network.result.fold
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val messageApiRepository: MessageApiRepository,
) : BaseViewModel<ChatState, ChatEvent, ChatAction>(
    initialState = savedStateHandle[KEY] ?: ChatState()
) {

    private val currentUserId = authRepository.userStateFlow.value.userId

    init {
        val conversationId = savedStateHandle.get<String>("conversationId").orEmpty()
        val participantName = savedStateHandle.get<String>("participantName").orEmpty()
        setState {
            state.copy(
                conversationId = conversationId,
                participantName = participantName,
            )
        }
        if (conversationId.isNotBlank()) {
            loadMessages()
        }
    }

    override fun handleAction(action: ChatAction) {
        when (action) {
            is ChatAction.OnMessageInputChanged -> {
                setState { state.copy(messageInput = action.text) }
            }
            is ChatAction.OnSendClicked -> sendMessage()
            is ChatAction.OnRefresh -> loadMessages()
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            setState { state.copy(isLoading = true) }
            messageApiRepository.getMessages(state.conversationId).fold(
                onSuccess = { messages ->
                    val uiModels = messages.map { msg ->
                        ChatMessageUiModel(
                            messageId = msg.id,
                            content = msg.content.orEmpty(),
                            isFromMe = msg.senderId == currentUserId,
                            formattedTimestamp = formatTime(msg.createdAt),
                            status = msg.status,
                        )
                    }
                    setState { state.copy(messages = uiModels, isLoading = false) }
                },
                onFailure = { exception ->
                    Timber.e("Failed to load messages: %s", exception.throwable.message)
                    setState { state.copy(isLoading = false) }
                    sendEvent(
                        ChatEvent.ShowSnackbar(
                            message = "Failed to load messages",
                            type = EchoSnackbarType.ERROR,
                        )
                    )
                },
            )
        }
    }

    private fun sendMessage() {
        val text = state.messageInput.trim()
        if (text.isBlank()) return

        setState { state.copy(messageInput = "", isSending = true) }

        viewModelScope.launch {
            messageApiRepository.sendMessage(
                conversationId = state.conversationId,
                content = text,
                messageType = MessageType.TEXT,
            ).fold(
                onSuccess = { response ->
                    val newMessage = ChatMessageUiModel(
                        messageId = response.id,
                        content = response.content.orEmpty(),
                        isFromMe = true,
                        formattedTimestamp = formatTime(response.createdAt),
                        status = response.status,
                    )
                    setState {
                        state.copy(
                            messages = state.messages + newMessage,
                            isSending = false,
                        )
                    }
                },
                onFailure = { exception ->
                    Timber.e("Failed to send message: %s", exception.throwable.message)
                    setState { state.copy(messageInput = text, isSending = false) }
                    sendEvent(
                        ChatEvent.ShowSnackbar(
                            message = "Failed to send message",
                            type = EchoSnackbarType.ERROR,
                        )
                    )
                },
            )
        }
    }

    companion object {
        private const val KEY = "ChatViewModel"
    }
}

// ── State ────────────────────────────────────────────────────────────

@Parcelize
data class ChatState(
    val conversationId: String = "",
    val participantName: String = "",
    val messages: List<ChatMessageUiModel> = emptyList(),
    val messageInput: String = "",
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
) : Parcelable

@Parcelize
data class ChatMessageUiModel(
    val messageId: String,
    val content: String,
    val isFromMe: Boolean,
    val formattedTimestamp: String?,
    val status: String?,
) : Parcelable

// ── Events ───────────────────────────────────────────────────────────

sealed interface ChatEvent {
    data class ShowSnackbar(
        val message: String,
        val type: EchoSnackbarType = EchoSnackbarType.ERROR,
    ) : ChatEvent
}

// ── Actions ──────────────────────────────────────────────────────────

sealed interface ChatAction {
    data class OnMessageInputChanged(val text: String) : ChatAction
    data object OnSendClicked : ChatAction
    data object OnRefresh : ChatAction
}

// ── Helpers ──────────────────────────────────────────────────────────

private val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
    isLenient = true
}

private fun formatTime(isoString: String?): String? {
    if (isoString == null) return null
    return try {
        val cleaned = isoString
            .replace("Z", "+0000")
            .replace(Regex("([+-]\\d{2}):(\\d{2})$"), "$1$2")
        val date = isoFormat.parse(cleaned) ?: return null
        timeFormat.format(date)
    } catch (e: Exception) {
        null
    }
}
