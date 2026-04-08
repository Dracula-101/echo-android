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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoIconButton
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
                        icon = IconResource.Vector(Icons.AutoMirrored.Filled.Logout),
                        onClick = { viewModel.trySendAction(ConversationAction.OnLogout) },
                    )
                },
            )
        },
        snackbarHost = {
            EchoSnackbarHost(state = snackbarState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigateToAddUser()
                },
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
        ConversationContent(
            state = state,
            onAction = viewModel::trySendAction,
            onNavigateToChat = navigateToChat,
        )
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
            conversations = state.conversations,
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
            modifier = Modifier.size(48.dp),
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
            .padding(EchoTheme.spacing.padding.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.4f),
        )
        EchoSpacer(size = EchoSpacerSize.Medium)
        Text(
            text = "No conversations yet",
            style = EchoTheme.typography.titleMedium,
            color = EchoTheme.colorScheme.surface.onColor,
        )
        EchoSpacer(size = EchoSpacerSize.Small)
        Text(
            text = "Start a new conversation using the + button",
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ConversationList(
    conversations: List<ConversationItemUiModel>,
    onNavigateToChat: (conversationId: String, participantName: String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = EchoTheme.spacing.padding.small),
    ) {
        items(
            items = conversations,
            key = { it.conversationId },
        ) { conversation ->
            ConversationListItem(
                conversation = conversation,
                onClick = {
                    onNavigateToChat(
                        conversation.conversationId,
                        conversation.participantName,
                    )
                },
            )
        }
    }
}

@Composable
private fun ConversationListItem(
    conversation: ConversationItemUiModel,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = EchoTheme.spacing.padding.medium,
                vertical = EchoTheme.spacing.padding.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            name = conversation.participantName,
            avatarUrl = conversation.participantAvatarUrl,
        )

        Spacer(Modifier.width(EchoTheme.spacing.gap.medium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.participantName,
                style = EchoTheme.typography.titleSmall,
                color = EchoTheme.colorScheme.surface.onColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (conversation.lastMessageContent != null) {
                Text(
                    text = buildString {
                        if (conversation.isLastMessageFromMe) append("You: ")
                        append(conversation.lastMessageContent)
                    },
                    style = EchoTheme.typography.bodySmall,
                    color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else{
                Text(
                    text = "No messages yet",
                    style = EchoTheme.typography.bodySmall,
                    color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.6f),
                )
            }
        }

        if (conversation.formattedTimestamp != null) {
            Spacer(Modifier.width(EchoTheme.spacing.gap.small))
            Text(
                text = conversation.formattedTimestamp,
                style = EchoTheme.typography.labelSmall,
                color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
private fun Avatar(
    name: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
) {
    if (avatarUrl != null) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = name,
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(EchoTheme.colorScheme.primary.container),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                style = EchoTheme.typography.titleMedium,
                color = EchoTheme.colorScheme.primary.onContainer,
            )
        }
    }
}
