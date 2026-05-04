package com.application.echo.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.features.messaging.model.MessageDeliveryStatus
import com.application.echo.ui.components.icon.EchoIconButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.scaffold.model.rememberEchoPullToRefreshState
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.components.topbar.EchoTopBar
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarState = rememberEchoSnackbarState()
    val listState = rememberLazyListState()

    DisposableEffect(Unit) {
        viewModel.trySendAction(ChatAction.OnScreenVisible)
        onDispose {
            viewModel.trySendAction(ChatAction.OnScreenHidden)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ChatEvent.ShowSnackbar -> snackbarState.show(
                    message = event.message,
                    type = event.type,
                )

                is ChatEvent.ScrollToBottom -> {
                    if (state.messages.isNotEmpty()) {
                        listState.animateScrollToItem(state.messages.lastIndex)
                    }
                }
            }
        }
    }

    LaunchedEffect(state.messages.size, state.typingUsers.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    EchoScaffold(
        topBar = {
            EchoTopBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = state.participantName.ifBlank { "Chat" },
                            style = EchoTheme.typography.titleLarge,
                            color = EchoTheme.colorScheme.surface.onColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = if (state.typingUsers.isNotEmpty()) "Typing…" else "Conversation",
                            style = EchoTheme.typography.labelMedium,
                            color = if (state.typingUsers.isNotEmpty()) {
                                EchoTheme.colorScheme.primary.color
                            } else {
                                EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.5f)
                            },
                            maxLines = 1,
                        )
                    }
                },
                navigationIcon = {
                    EchoIconButton(
                        icon = IconResource.Vector(Icons.AutoMirrored.Filled.ArrowBack),
                        onClick = onNavigateBack,
                    )
                },
            )
        },
        snackbarHost = {
            EchoSnackbarHost(state = snackbarState)
        },
        pullToRefreshState = rememberEchoPullToRefreshState(
            isEnabled = !state.isLoading && !state.isRefreshing,
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.trySendAction(ChatAction.OnRefresh) },
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(EchoTheme.colorScheme.background.color)
                .imePadding(),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when {
                    state.isLoading -> LoadingState()
                    state.messages.isEmpty() -> EmptyState()
                    else -> MessageTimeline(
                        state = state,
                        listState = listState,
                        onLoadOlder = { viewModel.trySendAction(ChatAction.OnLoadOlder) },
                        onRetryMessage = { localId ->
                            viewModel.trySendAction(ChatAction.OnRetryMessage(localId))
                        },
                    )
                }
            }

            MessageComposer(
                input = state.messageInput,
                isSending = state.isSending,
                onInputChanged = { viewModel.trySendAction(ChatAction.OnMessageInputChanged(it)) },
                onSend = { viewModel.trySendAction(ChatAction.OnSendClicked) },
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = EchoTheme.colorScheme.primary.color,
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(EchoTheme.spacing.padding.large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "No messages yet",
            style = EchoTheme.typography.titleLarge,
            color = EchoTheme.colorScheme.surface.onColor,
        )
        Spacer(modifier = Modifier.size(EchoTheme.spacing.gap.small))
        Text(
            text = "Send the first message to start this thread.",
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MessageTimeline(
    state: ChatState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onLoadOlder: () -> Unit,
    onRetryMessage: (String) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = 8.dp,
            bottom = 6.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        item(key = "history-loader") {
            AnimatedVisibility(
                visible = state.hasMoreHistory || state.isLoadingOlder,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    FilledTonalButton(
                        onClick = onLoadOlder,
                        enabled = !state.isLoadingOlder,
                    ) {
                        if (state.isLoadingOlder) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text("Load older messages")
                        }
                    }
                }
            }
        }

        itemsIndexed(
            items = state.messages,
            key = { _, message -> message.messageId },
        ) { index, message ->
            val previousMessage = state.messages.getOrNull(index - 1)
            val nextMessage = state.messages.getOrNull(index + 1)
            val currentDateLabel = formatDateLabel(message.rawTimestamp)
            val previousDateLabel = previousMessage?.rawTimestamp?.let(::formatDateLabel)
            val groupedWithPrevious = previousMessage?.isFromMe == message.isFromMe &&
                previousDateLabel == currentDateLabel
            val groupedWithNext = nextMessage?.isFromMe == message.isFromMe &&
                nextMessage.rawTimestamp.let(::formatDateLabel) == currentDateLabel

            if (currentDateLabel != previousDateLabel) {
                DateDivider(label = currentDateLabel)
            }

            MessageBubble(
                message = message,
                isGroupedWithPrevious = groupedWithPrevious,
                isGroupedWithNext = groupedWithNext,
                onRetryMessage = onRetryMessage,
            )
        }
    }
}

@Composable
private fun DateDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = EchoTheme.spacing.padding.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.small),
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = EchoTheme.colorScheme.outline.color.copy(alpha = 0.25f),
        )
        Text(
            text = label,
            style = EchoTheme.typography.labelMedium,
            color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.58f),
            modifier = Modifier.padding(horizontal = 6.dp),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = EchoTheme.colorScheme.outline.color.copy(alpha = 0.25f),
        )
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessageUiModel,
    isGroupedWithPrevious: Boolean,
    isGroupedWithNext: Boolean,
    onRetryMessage: (String) -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxBubbleWidth = screenWidth * 0.76f
    val shape = RoundedCornerShape(
        topStart = if (!message.isFromMe && isGroupedWithPrevious) 10.dp else 20.dp,
        topEnd = if (message.isFromMe && isGroupedWithPrevious) 10.dp else 20.dp,
        bottomStart = if (!message.isFromMe && isGroupedWithNext) 10.dp else 20.dp,
        bottomEnd = if (message.isFromMe && isGroupedWithNext) 10.dp else 20.dp,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isGroupedWithPrevious) 0.dp else 6.dp),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start,
    ) {
        Surface(
            modifier = Modifier.widthIn(max = maxBubbleWidth),
            shape = shape,
            color = if (message.isFromMe) {
                EchoTheme.colorScheme.primary.color
            } else {
                EchoTheme.colorScheme.surface.container
            },
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            SelectionContainer {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 11.dp,
                        vertical = 8.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = message.content.ifBlank { " " },
                        style = EchoTheme.typography.bodyLarge,
                        color = if (message.isFromMe) {
                            EchoTheme.colorScheme.primary.onColor
                        } else {
                            EchoTheme.colorScheme.surface.onColor
                        },
                    )

                    if (!isGroupedWithNext) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (message.formattedTimestamp != null) {
                                Text(
                                    text = message.formattedTimestamp,
                                    style = EchoTheme.typography.labelSmall,
                                    color = if (message.isFromMe) {
                                        EchoTheme.colorScheme.primary.onColor.copy(alpha = 0.7f)
                                    } else {
                                        EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.5f)
                                    },
                                )
                            }

                            if (message.isFromMe) {
                                Spacer(modifier = Modifier.size(4.dp))
                                DeliveryStatusIcon(
                                    status = message.deliveryStatus,
                                    tint = if (message.deliveryStatus == MessageDeliveryStatus.FAILED) {
                                        EchoTheme.colorScheme.error.color
                                    } else {
                                        EchoTheme.colorScheme.primary.onColor.copy(alpha = 0.82f)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        if (message.isFromMe && message.deliveryStatus == MessageDeliveryStatus.FAILED && message.localId != null) {
            AssistChip(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(0.9f),
                onClick = { onRetryMessage(message.localId) },
                label = {
                    Text(
                        text = "Tap to retry",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
        }
    }
}

@Composable
private fun DeliveryStatusIcon(
    status: MessageDeliveryStatus,
    tint: androidx.compose.ui.graphics.Color,
) {
    val icon = when (status) {
        MessageDeliveryStatus.PENDING -> Icons.Rounded.Schedule
        MessageDeliveryStatus.SENT -> Icons.Rounded.Done
        MessageDeliveryStatus.DELIVERED,
        MessageDeliveryStatus.READ,
        -> Icons.Rounded.DoneAll

        MessageDeliveryStatus.FAILED -> Icons.Outlined.ErrorOutline
    }

    Icon(
        imageVector = icon,
        contentDescription = status.name.lowercase(),
        modifier = Modifier.size(15.dp),
        tint = tint,
    )
}

@Composable
private fun MessageComposer(
    input: String,
    isSending: Boolean,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = EchoTheme.spacing.padding.medium,
                vertical = EchoTheme.spacing.padding.small,
            ),
        shape = RoundedCornerShape(24.dp),
        color = EchoTheme.colorScheme.surface.container,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EchoTheme.spacing.padding.small),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.small),
        ) {
            EchoTextField(
                value = input,
                onValueChange = onInputChanged,
                placeholder = "Message",
                singleLine = false,
                maxLines = 4,
                enabled = !isSending,
                modifier = Modifier.weight(1f),
            )

            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clickable(enabled = input.isNotBlank() && !isSending, onClick = onSend),
                shape = CircleShape,
                color = if (input.isNotBlank() && !isSending) {
                    EchoTheme.colorScheme.primary.color
                } else {
                    EchoTheme.colorScheme.surface.low
                },
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = EchoTheme.colorScheme.primary.onColor,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (input.isNotBlank()) {
                                EchoTheme.colorScheme.primary.onColor
                            } else {
                                EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.35f)
                            },
                        )
                    }
                }
            }
        }
    }
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
    isLenient = true
}
private val monthDayFormat = SimpleDateFormat("MMM d", Locale.US)
private val fullDateFormat = SimpleDateFormat("EEE, MMM d", Locale.US)
private val fullDateWithYearFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

private fun formatDateLabel(isoString: String): String {
    return try {
        val cleaned = isoString
            .replace("Z", "+0000")
            .replace(Regex("([+-]\\d{2}):(\\d{2})$"), "$1$2")

        val date = dateFormat.parse(cleaned) ?: return "Recent"
        val now = Calendar.getInstance()
        val then = Calendar.getInstance().apply { time = date }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val comparedDay = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val daysDiff = ((today.timeInMillis - comparedDay.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
        when {
            daysDiff <= 0 -> "Today"
            daysDiff == 1 -> "Yesterday"
            now.get(Calendar.YEAR) == then.get(Calendar.YEAR) -> fullDateFormat.format(date)
            else -> fullDateWithYearFormat.format(date)
        }
    } catch (_: Exception) {
        monthDayFormat.format(Date())
    }
}
