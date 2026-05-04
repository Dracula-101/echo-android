package com.application.echo.presentation.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.presentation.common.EchoAvatar
import com.application.echo.ui.components.badge.NotificationBadge
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.icon.EchoIconButton
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.divider.EchoDivider
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.scaffold.model.rememberEchoPullToRefreshState
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.spacing.EchoSpacer
import com.application.echo.ui.components.spacing.EchoSpacerSize
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.components.topbar.EchoTopBar
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.design.R
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
                title = "echo",
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_app_logo_grayscale),
                        contentDescription = "Echo",
                        modifier = Modifier.size(EchoTheme.dimen.icon.large),
                    )
                },
                actions = {
                    EchoIconButton(
                        icon = IconResource.Vector(Icons.Outlined.QrCode2),
                        onClick = {
                            navigateToSettings()
                        },
                        variant = EchoVariant.Primary,
                    )
                    EchoIconButton(
                        icon = IconResource.Vector(Icons.Outlined.PhotoCamera),
                        onClick = {
                            viewModel.trySendAction(ConversationAction.OnLogout)
                        },
                        variant = EchoVariant.Primary,
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
    Column {
        EchoTextField(
            value = "",
            onValueChange = {

            },
            leading = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.55f),
                    modifier = Modifier.size(EchoTheme.dimen.icon.small)
                )
            },
            placeholder = "Search people, messages, links...",
            placeholderTextStyle = EchoTheme.typography.bodyMedium.copy(
                color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.55f),
            ),
            contentPadding = PaddingValues(
                top = EchoTheme.spacing.padding.medium,
                bottom = 1.5 * EchoTheme.spacing.padding.small,
                start = EchoTheme.spacing.padding.medium,
                end = EchoTheme.spacing.padding.medium,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = EchoTheme.spacing.padding.small,
                    vertical = EchoTheme.spacing.padding.small,
                ),
        )
        Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.small))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Row(
                    modifier = Modifier
                        .height(64.dp + EchoTheme.spacing.padding.medium)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = EchoTheme.spacing.padding.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.medium),
                ) {
                    MyStatus(
                        initial = "P",
                        label = "You",
                        onClick = { /* TODO: Navigate to My Status screen */ },
                    )

                    sampleFriends.forEach { friend ->
                        FriendStatus(
                            initial = friend.initial,
                            label = friend.label,
                            textColor = friend.textColor,
                            isActive = friend.isActive,
                        )
                    }
                }
            }
            when {
                state.isLoading -> LoadingState()
                state.errorMessage != null && state.conversations.isEmpty() -> ErrorState(
                    message = state.errorMessage,
                    onRetry = { onAction(ConversationAction.OnRetry) },
                )
                // state.conversations.isEmpty() -> EmptyState()
                else -> ConversationList(
                    conversations = sampleConversations,
                    onNavigateToChat = onNavigateToChat,
                )
            }
        }
    }
}


private fun LazyListScope.LoadingState() {
    item {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = EchoTheme.colorScheme.primary.color,
            )
        }
    }
}

private fun LazyListScope.ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    item {
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

private fun LazyListScope.ConversationList(
    conversations: List<ConversationItem>,
    onNavigateToChat: (conversationId: String, participantName: String) -> Unit,
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
        EchoDivider(
            modifier = Modifier.padding(start = 76.dp),
            spacing = 0.dp,
        )
    }
}