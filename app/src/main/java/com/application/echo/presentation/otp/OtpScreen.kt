package com.application.echo.presentation.otp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
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
    viewModel: OtpViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = rememberEchoSnackbarState()

    LaunchedEffect(Unit) {
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
            "Sent to ${state.phoneNumber}",
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

        Spacer(modifier = Modifier.height(8.dp))

        // Expiry
        AnimatedVisibility(
            visible = !state.isExpired && state.expirySeconds > 0,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Text(
                text = "Code expires in ${state.expiryFormatted}",
                style = EchoTheme.typography.labelSmall,
                color = EchoTheme.colorScheme.scrim.color,
            )
        }

        Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.large))

        EchoLoadingButton(
            text = "Verify",
            loading = state.isLoading,
            enabled = state.isOtpComplete && !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onAction(OtpAction.OnVerifyClicked) },
        )

        Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.medium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Wrong number?",
                style = EchoTheme.typography.labelMedium,
                color = EchoTheme.colorScheme.primary.color,
                modifier = Modifier.clickable { onAction(OtpAction.OnEditPhoneClicked) },
            )
            if (state.canResend) {
                Text(
                    text = "Resend code",
                    style = EchoTheme.typography.labelMedium,
                    color = EchoTheme.colorScheme.primary.color,
                    modifier = Modifier.clickable { onAction(OtpAction.OnResendClicked) },
                )
            } else {
                Text(
                    text = "Resend in ${state.resendCooldownSeconds}s",
                    style = EchoTheme.typography.labelMedium,
                    color = EchoTheme.colorScheme.scrim.color,
                )
            }
        }
    }
}