package com.application.echo.presentation.create_profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.components.topbar.EchoTopBar
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    viewModel: CreateProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarState = rememberEchoSnackbarState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest {
            when(it) {
                is CreateProfileEvent.ShowSnackbar -> {
                    snackbarState.show(
                        message = it.message,
                        detail = it.detail,
                        code = it.code,
                    )
                }
            }
        }
    }

    EchoScaffold(
        topBar = {
            EchoTopBar(title = "Create Profile")
        },
        snackbarHost = {
            EchoSnackbarHost(
                state = snackbarState,
                modifier = Modifier.imePadding()
            )
        },
        modifier = Modifier
            .imePadding()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = EchoTheme.spacing.padding.medium)
                    .padding(bottom = 80.dp), // room for pinned button
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                AvatarPicker(
                    avatarUri = uiState.avatarUri,
                    avatarUrl = uiState.avatarUrl,
                    onPickAvatar = { uri->
                        viewModel.trySendAction(CreateProfileAction.OnAvatarSelected(uri))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                EchoTextField(
                    value = uiState.displayName.orEmpty(),
                    onValueChange = { displayName ->
                        viewModel.trySendAction(CreateProfileAction.OnDisplayNameChanged(displayName))
                    },
                    label = "Display Name",
                    placeholder = "@username",
                    leading = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EchoTextField(
                        value = uiState.firstName.orEmpty(),
                        onValueChange = { firstName ->
                            viewModel.trySendAction(CreateProfileAction.OnFirstNameChanged(firstName))
                        },
                        label = "First Name",
                        placeholder = "John",
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    EchoTextField(
                        value = uiState.lastName.orEmpty(),
                        onValueChange = { lastName ->
                            viewModel.trySendAction(CreateProfileAction.OnLastNameChanged(lastName))
                        },
                        label = "Last Name",
                        placeholder = "Doe",
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Pinned button — always visible above keyboard
            EchoFilledButton(
                text = "Save Profile",
                onClick = {
                    keyboardController?.hide()
                    viewModel.trySendAction(CreateProfileAction.OnSaveClicked)
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = EchoTheme.spacing.padding.medium)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun AvatarPicker(
    avatarUri: Uri?,
    avatarUrl: String?,
    onPickAvatar: (Uri) -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { onPickAvatar(it) } }

    Box(contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(EchoTheme.colorScheme.surface.variant)
                .clickable {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (avatarUrl != null || avatarUri != null) {
                AsyncImage(
                    model = avatarUrl ?: avatarUri,
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = EchoTheme.colorScheme.surface.variant
                )
            }
        }

        Surface(
            shape = CircleShape,
            color = EchoTheme.colorScheme.primary.color,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Change photo",
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxSize(),
                tint = EchoTheme.colorScheme.primary.onColor
            )
        }
    }
}