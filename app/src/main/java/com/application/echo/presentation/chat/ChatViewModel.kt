package com.application.echo.presentation.chat

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.application.echo.api.message.MessageType
import com.application.echo.core.common.platform.base.BaseViewModel
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.messaging.model.Message
import com.application.echo.features.messaging.model.MessageDeliveryStatus
import com.application.echo.features.messaging.model.MessagingEvent
import com.application.echo.features.messaging.repository.MessagingRepository
import com.application.echo.ui.components.snackbar.EchoSnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
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
    private val messagingRepository: MessagingRepository,
) : BaseViewModel<ChatState, ChatEvent, ChatAction>(
    initialState = savedStateHandle[KEY] ?: ChatState()
) {

    private val currentUserId = authRepository.userStateFlow.value.userId
    private var typingStopJob: Job? = null

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
            messagingRepository.setActiveConversation(conversationId)
            observeMessages(conversationId)
            observeTyping(conversationId)
            observeEvents(conversationId)
            refreshMessages(conversationId)
        }
    }

    override fun handleAction(action: ChatAction) {
        when (action) {
            is ChatAction.OnMessageInputChanged -> {
                updateTypingState(previousValue = state.messageInput, nextValue = action.text)
                setState { state.copy(messageInput = action.text) }
            }
            is ChatAction.OnSendClicked -> sendMessage()
            is ChatAction.OnRefresh -> refreshMessages(state.conversationId)
            is ChatAction.OnScreenVisible -> onScreenVisible()
            is ChatAction.OnScreenHidden -> onScreenHidden()
            is ChatAction.OnLoadOlder -> loadOlderMessages()
            is ChatAction.OnRetryMessage -> retryMessage(action.localId)
        }
    }

    override fun onCleared() {
        stopTyping()
        super.onCleared()
        messagingRepository.setActiveConversation(null)
    }

    private fun observeMessages(conversationId: String) {
        messagingRepository.messages(conversationId)
            .onEach { messages ->
                val uiModels = messages.map { it.toUiModel() }
                setState {
                    state.copy(
                        messages = uiModels,
                        isLoading = false,
                        isRefreshing = false,
                    )
                }
                savedStateHandle[KEY] = state
            }
            .catch { e ->
                Timber.e(e, "Error observing messages")
                setState { state.copy(isLoading = false, isRefreshing = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeTyping(conversationId: String) {
        messagingRepository.typingUsers(conversationId)
            .distinctUntilChanged()
            .onEach { typingUserIds ->
                setState { state.copy(typingUsers = typingUserIds) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeEvents(conversationId: String) {
        messagingRepository.events
            .filter { event ->
                when (event) {
                    is MessagingEvent.NewMessageReceived -> event.conversationId == conversationId
                    is MessagingEvent.MessageSendFailed -> event.conversationId == conversationId
                    is MessagingEvent.MessageSendConfirmed -> event.conversationId == conversationId
                    is MessagingEvent.ConversationCreated -> false
                }
            }
            .onEach { event ->
                when (event) {
                    is MessagingEvent.NewMessageReceived -> {
                        sendEvent(ChatEvent.ScrollToBottom)
                        onScreenVisible()
                    }
                    is MessagingEvent.MessageSendFailed -> {
                        sendEvent(
                            ChatEvent.ShowSnackbar(
                                message = "Failed to send message",
                                type = EchoSnackbarType.ERROR,
                            )
                        )
                    }
                    is MessagingEvent.MessageSendConfirmed -> {
                        sendEvent(ChatEvent.ScrollToBottom)
                    }
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshMessages(conversationId: String) {
        if (conversationId.isBlank()) return
        viewModelScope.launch {
            setState {
                state.copy(
                    isLoading = state.messages.isEmpty(),
                    isRefreshing = state.messages.isNotEmpty(),
                )
            }
            try {
                messagingRepository.refreshMessages(conversationId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to refresh messages")
                setState { state.copy(isLoading = false, isRefreshing = false) }
                sendEvent(
                    ChatEvent.ShowSnackbar(
                        message = "Failed to load messages",
                        type = EchoSnackbarType.ERROR,
                    )
                )
            }
        }
    }

    private fun loadOlderMessages() {
        if (state.conversationId.isBlank() || state.isLoadingOlder || !state.hasMoreHistory) return

        viewModelScope.launch {
            setState { state.copy(isLoadingOlder = true) }
            try {
                val hasMore = messagingRepository.loadOlderMessages(state.conversationId)
                setState {
                    state.copy(
                        isLoadingOlder = false,
                        hasMoreHistory = hasMore,
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load older messages")
                setState { state.copy(isLoadingOlder = false) }
                sendEvent(
                    ChatEvent.ShowSnackbar(
                        message = "Could not load older messages",
                        type = EchoSnackbarType.ERROR,
                    )
                )
            }
        }
    }

    private fun sendMessage() {
        val text = state.messageInput.trim()
        if (text.isBlank()) return

        stopTyping()
        setState { state.copy(messageInput = "", isSending = true) }

        viewModelScope.launch {
            try {
                messagingRepository.sendMessage(
                    conversationId = state.conversationId,
                    content = text,
                    messageType = MessageType.TEXT,
                )
                setState { state.copy(isSending = false) }
                sendEvent(ChatEvent.ScrollToBottom)
            } catch (e: Exception) {
                Timber.e(e, "Failed to send message")
                setState { state.copy(messageInput = text, isSending = false) }
                sendEvent(
                    ChatEvent.ShowSnackbar(
                        message = "Failed to send message",
                        type = EchoSnackbarType.ERROR,
                    )
                )
            }
        }
    }

    private fun retryMessage(localId: String) {
        viewModelScope.launch {
            val retried = messagingRepository.retryMessage(localId)
            if (!retried) {
                sendEvent(
                    ChatEvent.ShowSnackbar(
                        message = "Unable to retry message",
                        type = EchoSnackbarType.ERROR,
                    )
                )
            }
        }
    }

    private fun onScreenVisible() {
        if (state.conversationId.isBlank()) return
        messagingRepository.setActiveConversation(state.conversationId)
        // Mark all unread incoming messages as read
        viewModelScope.launch {
            val unreadIds = state.messages
                .filter { !it.isFromMe && it.deliveryStatus != MessageDeliveryStatus.READ }
                .map { it.messageId }
            if (unreadIds.isNotEmpty()) {
                messagingRepository.markAsRead(state.conversationId, unreadIds)
            }
        }
    }

    private fun onScreenHidden() {
        stopTyping()
        messagingRepository.setActiveConversation(null)
    }

    private fun updateTypingState(previousValue: String, nextValue: String) {
        if (state.conversationId.isBlank()) return

        val wasTyping = previousValue.isNotBlank()
        val isTyping = nextValue.isNotBlank()

        when {
            !wasTyping && isTyping -> messagingRepository.sendTyping(state.conversationId, true)
            wasTyping && !isTyping -> stopTyping()
        }

        if (isTyping) {
            typingStopJob?.cancel()
            typingStopJob = viewModelScope.launch {
                delay(TYPING_STOP_DELAY_MS)
                messagingRepository.sendTyping(state.conversationId, false)
            }
        }
    }

    private fun stopTyping() {
        typingStopJob?.cancel()
        if (state.conversationId.isNotBlank()) {
            messagingRepository.sendTyping(state.conversationId, false)
        }
    }

    private fun Message.toUiModel(): ChatMessageUiModel = ChatMessageUiModel(
        messageId = id,
        localId = localId,
        content = content,
        isFromMe = isOutgoing,
        formattedTimestamp = formatTime(createdAt),
        rawTimestamp = createdAt,
        deliveryStatus = status,
    )

    companion object {
        private const val KEY = "ChatViewModel"
        private const val TYPING_STOP_DELAY_MS = 1_800L
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
    val isRefreshing: Boolean = false,
    val isLoadingOlder: Boolean = false,
    val isSending: Boolean = false,
    val hasMoreHistory: Boolean = true,
    val typingUsers: Set<String> = emptySet(),
) : Parcelable

@Parcelize
data class ChatMessageUiModel(
    val messageId: String,
    val localId: String?,
    val content: String,
    val isFromMe: Boolean,
    val formattedTimestamp: String?,
    val rawTimestamp: String,
    val deliveryStatus: MessageDeliveryStatus,
) : Parcelable

// ── Events ───────────────────────────────────────────────────────────

sealed interface ChatEvent {
    data class ShowSnackbar(
        val message: String,
        val type: EchoSnackbarType = EchoSnackbarType.ERROR,
    ) : ChatEvent

    data object ScrollToBottom : ChatEvent
}

// ── Actions ──────────────────────────────────────────────────────────

sealed interface ChatAction {
    data class OnMessageInputChanged(val text: String) : ChatAction
    data object OnSendClicked : ChatAction
    data object OnRefresh : ChatAction
    data object OnScreenVisible : ChatAction
    data object OnScreenHidden : ChatAction
    data object OnLoadOlder : ChatAction
    data class OnRetryMessage(val localId: String) : ChatAction
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
