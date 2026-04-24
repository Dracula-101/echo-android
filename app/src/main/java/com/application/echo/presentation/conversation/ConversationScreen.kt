package com.application.echo.presentation.conversation

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.presentation.common.EchoAvatar
import com.application.echo.ui.components.badge.NotificationBadge
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoIconButton
import com.application.echo.ui.components.divider.EchoDivider
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.scaffold.model.rememberEchoPullToRefreshState
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.spacing.EchoSpacer
import com.application.echo.ui.components.spacing.EchoSpacerSize
import com.application.echo.ui.components.topbar.EchoTopBar
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel = hiltViewModel(),
    navigateToAddUser: () -> Unit,
    navigateToChat: (conversationId: String, participantName: String) -> Unit,
    navigateToSettings: () -> Unit = {},
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarState = rememberEchoSnackbarState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ConversationEvent.ShowSnackbar -> snackbarState.show(
                    message = event.message,
                    detail = event.detail,
                    code = event.code,
                    type = event.type,
                )
            }
        }
    }

    EchoScaffold(
        topBar = {
            EchoTopBar(
                title = "Messages",
                actions = {
                    EchoIconButton(
                        icon = IconResource.Vector(Icons.Default.Settings),
                        onClick = navigateToSettings,
                    )
                },
            )
        },
        snackbarHost = {
            EchoSnackbarHost(state = snackbarState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAddUser,
                containerColor = EchoTheme.colorScheme.primary.color,
                contentColor = EchoTheme.colorScheme.primary.onColor,
            ) {
                Icon(Icons.Default.Add, contentDescription = "New conversation")
            }
        },
        pullToRefreshState = rememberEchoPullToRefreshState(
            isEnabled = !state.isLoading,
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.trySendAction(ConversationAction.OnRefresh) },
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EchoTheme.colorScheme.background.color),
        ) {
            ConversationContent(
                state = state,
                onAction = viewModel::trySendAction,
                onNavigateToChat = navigateToChat,
            )
        }
    }
}

@Composable
private fun ConversationContent(
    state: ConversationState,
    onAction: (ConversationAction) -> Unit,
    onNavigateToChat: (conversationId: String, participantName: String) -> Unit,
) {
    when {
        state.isLoading -> LoadingState()
        state.errorMessage != null && state.conversations.isEmpty() -> ErrorState(
            message = state.errorMessage,
            onRetry = { onAction(ConversationAction.OnRetry) },
        )
        state.conversations.isEmpty() -> EmptyState()
        else -> ConversationList(
            state = state,
            onNavigateToChat = onNavigateToChat,
        )
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
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(EchoTheme.spacing.padding.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = EchoTheme.colorScheme.error.color,
        )
        EchoSpacer(size = EchoSpacerSize.Medium)
        Text(
            text = message,
            style = EchoTheme.typography.bodyLarge,
            color = EchoTheme.colorScheme.surface.onColor,
            textAlign = TextAlign.Center,
        )
        EchoSpacer(size = EchoSpacerSize.Medium)
        EchoFilledButton(
            text = "Retry",
            onClick = onRetry,
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = EchoTheme.colorScheme.primary.color.copy(alpha = 0.6f),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "No conversations yet",
            style = EchoTheme.typography.titleLarge,
            color = EchoTheme.colorScheme.surface.onColor,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button to start chatting with someone",
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.55f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ConversationList(
    state: ConversationState,
    onNavigateToChat: (conversationId: String, participantName: String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        item(key = "summary") {
            Text(
                text = if (state.totalUnreadCount > 0) {
                    "${state.totalUnreadCount} unread"
                } else {
                    "${state.conversations.size} conversations"
                },
                style = EchoTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                color = if (state.totalUnreadCount > 0) {
                    EchoTheme.colorScheme.primary.color
                } else {
                    EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.55f)
                },
                modifier = Modifier.padding(
                    horizontal = EchoTheme.spacing.padding.medium,
                    vertical = EchoTheme.spacing.padding.small,
                ),
            )
        }

        items(
            items = state.conversations,
            key = { it.conversationId },
        ) { conversation ->
            ConversationRow(
                conversation = conversation,
                onClick = {
                    onNavigateToChat(
                        conversation.conversationId,
                        conversation.participantName,
                    )
                },
            )
            EchoDivider(
                modifier = Modifier.padding(start = 76.dp),
                spacing = 0.dp,
            )
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: ConversationItemUiModel,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = EchoTheme.spacing.padding.medium,
                vertical = 12.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EchoAvatar(
            name = conversation.participantName,
            avatarUrl = conversation.participantAvatarUrl,
            size = 52.dp,
            onlineStatus = conversation.onlineStatus,
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = conversation.participantName,
                    style = EchoTheme.typography.titleMedium.copy(
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.SemiBold else FontWeight.Medium,
                    ),
                    color = EchoTheme.colorScheme.surface.onColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (conversation.formattedTimestamp != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = conversation.formattedTimestamp,
                        style = EchoTheme.typography.labelSmall,
                        color = if (conversation.unreadCount > 0) {
                            EchoTheme.colorScheme.primary.color
                        } else {
                            EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.45f)
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = buildPreviewText(conversation),
                    style = EchoTheme.typography.bodyMedium.copy(
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                    ),
                    color = EchoTheme.colorScheme.surface.onColor.copy(
                        alpha = if (conversation.unreadCount > 0) 0.8f else 0.55f,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    NotificationBadge(
                        notificationCount = conversation.unreadCount,
                        backgroundColor = EchoTheme.colorScheme.primary.color,
                        contentColor = EchoTheme.colorScheme.primary.onColor,
                    )
                }
            }
        }
    }
}

private fun buildPreviewText(conversation: ConversationItemUiModel): String {
    val body = conversation.lastMessageContent?.takeIf { it.isNotBlank() } ?: "No messages yet"
    return if (conversation.isLastMessageFromMe && body != "No messages yet") {
        "You: $body"
    } else {
        body
    }
}
