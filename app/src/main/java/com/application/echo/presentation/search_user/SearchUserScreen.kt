package com.application.echo.presentation.search_user

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.application.echo.ui.components.button.EchoIconButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.spacing.EchoSpacer
import com.application.echo.ui.components.spacing.EchoSpacerSize
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.components.topbar.EchoTopBar
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUserScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (conversationId: String, participantName: String) -> Unit,
    viewModel: SearchUserViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarState = rememberEchoSnackbarState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SearchUserEvent.ShowSnackbar -> snackbarState.show(
                    message = event.message,
                    type = event.type,
                )
                is SearchUserEvent.NavigateToChat -> {
                    onNavigateToChat(event.conversationId, event.participantName)
                }
            }
        }
    }

    EchoScaffold(
        topBar = {
            EchoTopBar(
                title = "Find People",
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
        Column(modifier = Modifier.fillMaxSize()) {
            EchoTextField(
                value = state.query,
                onValueChange = { viewModel.trySendAction(SearchUserAction.OnQueryChanged(it)) },
                placeholder = "Search by name or username",
                singleLine = true,
                leading = { _ ->
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.5f),
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = EchoTheme.spacing.padding.medium,
                        vertical = EchoTheme.spacing.padding.small,
                    )
                    .focusRequester(focusRequester),
            )

            when {
                state.isSearching -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(EchoTheme.spacing.padding.large),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = EchoTheme.colorScheme.primary.color,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                state.query.isNotBlank() && state.results.isEmpty() && !state.isSearching -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(EchoTheme.spacing.padding.large),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No users found",
                            style = EchoTheme.typography.bodyMedium,
                            color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.5f),
                        )
                    }
                }
                state.query.isBlank() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(EchoTheme.spacing.padding.large),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Search for people to start a conversation",
                            style = EchoTheme.typography.bodyMedium,
                            color = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.5f),
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = EchoTheme.spacing.padding.small),
                    ) {
                        items(
                            items = state.results,
                            key = { it.userId },
                        ) { user ->
                            SearchResultItem(
                                user = user,
                                isCreating = state.isCreating && state.creatingUserId == user.userId,
                                onClick = {
                                    viewModel.trySendAction(
                                        SearchUserAction.OnUserClicked(user.userId, user.displayName)
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    user: SearchResultUiModel,
    isCreating: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isCreating, onClick = onClick)
            .padding(
                horizontal = EchoTheme.spacing.padding.medium,
                vertical = EchoTheme.spacing.padding.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (user.avatarUrl != null) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = user.displayName,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(EchoTheme.colorScheme.primary.container),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = user.displayName.firstOrNull()?.uppercase() ?: "?",
                    style = EchoTheme.typography.titleMedium,
                    color = EchoTheme.colorScheme.primary.onContainer,
                )
            }
        }

        Spacer(Modifier.width(EchoTheme.spacing.gap.medium))

        Text(
            text = user.displayName,
            style = EchoTheme.typography.titleSmall,
            color = EchoTheme.colorScheme.surface.onColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        if (isCreating) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = EchoTheme.colorScheme.primary.color,
                strokeWidth = 2.dp,
            )
        }
    }
}
