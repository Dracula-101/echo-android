package com.application.echo.presentation.otp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.ui.components.button.EchoLoadingButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme

@Composable
fun OtpScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    phoneNumber: String,
    viewModel: OtpViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = rememberEchoSnackbarState()

    LaunchedEffect(Unit) {
        viewModel.setState { state.copy(phoneNumber = phoneNumber) }
        viewModel.eventFlow.collect { event ->
            when (event) {
                is OtpEvent.NavigateToHome -> onNavigateToHome()
                is OtpEvent.NavigateBack -> onNavigateBack()
                is OtpEvent.ShowSnackbar -> snackbarHostState.show(
                    message = event.message,
                    detail = event.detail,
                    code = event.code,
                    type = event.type,
                )
            }
        }
    }

    EchoScaffold {
        OtpContent(state = state, onAction = viewModel::trySendAction)
    }
}

@Composable
private fun OtpContent(
    state: OtpState,
    onAction: (OtpAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EchoTheme.colorScheme.background.color)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(EchoTheme.spacing.padding.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.padding(EchoTheme.spacing.gap.extraLarge))
        AppHeader()
        Spacer(modifier = Modifier.padding(EchoTheme.spacing.gap.extraLarge))
        OtpForm(state = state, onAction = onAction)
    }
}

@Composable
private fun AppHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(EchoTheme.dimen.icon.extraLarge),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_app_logo_grayscale),
            contentDescription = "App Logo",
        )
        Text(
            "echo",
            style = EchoTheme.typography.titleLarge,
            color = EchoTheme.colorScheme.background.onColor,
        )
    }
}

@Composable
private fun OtpForm(
    state: OtpState,
    onAction: (OtpAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Enter the code",
            style = EchoTheme.typography.headlineMedium,
            color = EchoTheme.colorScheme.background.onColor,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            buildAnnotatedString {
                append("Sent to +${state.phoneNumber}")
                append("  ")
                append("Change")
                addStyle(
                    style = EchoTheme.typography.bodyMedium.toSpanStyle().copy(
                        color = EchoTheme.colorScheme.surface.onColor,
                        textDecoration = TextDecoration.Underline
                    ),
                    start = length - 6,
                    end = length,
                )
            },
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.scrim.color,
        )
        Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.large))

        OtpInputField(
            digits = state.otpDigits,
            isError = state.otpError != null,
            onDigitChanged = { index, digit -> onAction(OtpAction.OnDigitChanged(index, digit)) },
            onPaste = { onAction(OtpAction.OnPaste(it)) },
            onBackspace = { onAction(OtpAction.OnBackspace(it)) },
            onFilled = { onAction(OtpAction.OnFilled) },
        )

        // Error
        AnimatedVisibility(
            visible = state.otpError != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.otpError.orEmpty(),
                    style = EchoTheme.typography.labelSmall,
                    color = EchoTheme.colorScheme.error.color,
                )
            }
        }

        Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.large))

        Row(
            modifier = Modifier
                .clip(EchoTheme.shapes.chip)
                .background(EchoTheme.colorScheme.primary.container)
                .clickable(enabled = state.canResend) { onAction(OtpAction.OnResendClicked) }
                .padding(vertical = EchoTheme.spacing.padding.small, horizontal = EchoTheme.spacing.padding.medium),
            horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Resend Icon",
                tint = if(state.canResend) EchoTheme.colorScheme.primary.onColor else EchoTheme.colorScheme.primary.dim,
                modifier = Modifier.size(EchoTheme.dimen.icon.small),
            )
            if (state.canResend) {
                Text(
                    text = "Resend code",
                    style = EchoTheme.typography.bodyMedium,
                    color = EchoTheme.colorScheme.primary.onColor,
                )
            } else {
                Text(
                    text = "Resend in ${state.resendCooldownSeconds}s",
                    style = EchoTheme.typography.bodyMedium,
                    color = EchoTheme.colorScheme.primary.dim,
                )
            }
        }
    }
}