package com.application.echo.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.ui.components.button.EchoIconButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.components.topbar.EchoTopBar
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarState = rememberEchoSnackbarState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ChatEvent.ShowSnackbar -> snackbarState.show(
                    message = event.message,
                    type = event.type,
                )
            }
        }
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    EchoScaffold(
        topBar = {
            EchoTopBar(
                title = state.participantName.ifBlank { "Chat" },
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            // Messages
            Box(modifier = Modifier.weight(1f)) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                color = EchoTheme.colorScheme.primary.color,
                            )
                        }
                    }
                    state.messages.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No messages yet.\nSend the first message!",
                                style = EchoTheme.typography.bodyMedium,
                                color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = EchoTheme.spacing.padding.medium,
                                vertical = EchoTheme.spacing.padding.small,
                            ),
                            verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.small),
                        ) {
                            items(
                                items = state.messages,
                                key = { it.messageId },
                            ) { message ->
                                MessageBubble(message = message)
                            }
                        }
                    }
                }
            }

            // Input bar
            MessageInputBar(
                input = state.messageInput,
                isSending = state.isSending,
                onInputChanged = { viewModel.trySendAction(ChatAction.OnMessageInputChanged(it)) },
                onSend = { viewModel.trySendAction(ChatAction.OnSendClicked) },
            )
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessageUiModel) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxBubbleWidth = screenWidth * 0.75f

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = maxBubbleWidth)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (message.isFromMe) 12.dp else 4.dp,
                        bottomEnd = if (message.isFromMe) 4.dp else 12.dp,
                    )
                )
                .background(
                    if (message.isFromMe) EchoTheme.colorScheme.primary.color
                    else EchoTheme.colorScheme.surface.container
                )
                .padding(
                    horizontal = EchoTheme.spacing.padding.small,
                    vertical = EchoTheme.spacing.padding.small,
                ),
        ) {
            Column {
                Text(
                    text = message.content,
                    style = EchoTheme.typography.bodyMedium,
                    color = if (message.isFromMe) EchoTheme.colorScheme.primary.onColor
                    else EchoTheme.colorScheme.surface.onColor,
                )
                if (message.formattedTimestamp != null) {
                    Text(
                        text = message.formattedTimestamp,
                        style = EchoTheme.typography.labelSmall,
                        color = if (message.isFromMe) EchoTheme.colorScheme.primary.onColor.copy(alpha = 0.7f)
                        else EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.5f),
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageInputBar(
    input: String,
    isSending: Boolean,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = EchoTheme.spacing.padding.small,
                vertical = EchoTheme.spacing.padding.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EchoTextField(
            value = input,
            onValueChange = onInputChanged,
            placeholder = "Type a message",
            singleLine = false,
            maxLines = 4,
            enabled = !isSending,
            modifier = Modifier.weight(1f),
        )

        if (isSending) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(start = EchoTheme.spacing.padding.small)
                    .size(24.dp),
                color = EchoTheme.colorScheme.primary.color,
                strokeWidth = 2.dp,
            )
        } else {
            IconButton(
                onClick = onSend,
                enabled = input.isNotBlank(),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (input.isNotBlank()) EchoTheme.colorScheme.primary.color
                    else EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.3f),
                )
            }
        }
    }
}
