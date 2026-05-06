package com.application.echo.presentation.otp

import android.app.Activity
import androidx.activity.compose.LocalActivity
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.ui.components.icon.EchoIconButton
import com.application.echo.ui.components.icon.EchoIconButtonSize
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.EchoFlatSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme

@Composable
fun OtpScreen(
    onNavigateBack: () -> Unit,
    viewModel: OtpViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = rememberEchoSnackbarState()
    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is OtpEvent.NavigateBack -> onNavigateBack()
                is OtpEvent.RequestResend -> if(activity != null) viewModel.trySendAction(
                    OtpAction.OnResendWithContext(activity)
                )

                is OtpEvent.ShowSnackbar -> snackbarHostState.show(
                    message = event.message,
                    detail = event.detail,
                    code = event.code,
                    type = event.type,
                )
            }
        }
    }

    EchoScaffold(
        snackbarHost = {
            EchoFlatSnackbarHost(state = snackbarHostState)
        },
    ) {
        OtpContent(state = state, onAction = viewModel::trySendAction, onNavigateBack = onNavigateBack)
    }
}

@Composable
private fun OtpContent(
    state: OtpScreenState,
    onAction: (OtpAction) -> Unit,
    onNavigateBack: () -> Unit,
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
        Spacer(modifier = Modifier.padding(EchoTheme.spacing.gap.medium))
        EchoIconButton(
            onClick = {
                onNavigateBack()
            },
            circle = true,
            icon = IconResource.Vector(Icons.AutoMirrored.Outlined.ArrowBackIos),
            size = EchoIconButtonSize.Small,
            modifier = Modifier.align(Alignment.Start),
        )
        Spacer(modifier = Modifier.padding(EchoTheme.spacing.gap.medium))
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
    state: OtpScreenState,
    onAction: (OtpAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            buildAnnotatedString {
                append("Tap in the ")
                withStyle(
                    EchoTheme.typography.headlineMedium
                        .toSpanStyle()
                        .copy(color = EchoTheme.colorScheme.primary.color)
                ) {
                    append("code")
                }
            },
            style = EchoTheme.typography.headlineMedium,
            color = EchoTheme.colorScheme.background.onColor,
        )
        Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraSmall))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Sent to ${state.phoneInfo?.toDisplayString()}  ",
                style = EchoTheme.typography.bodyMedium,
                color = EchoTheme.colorScheme.scrim.color,
            )
            Text(
                modifier = Modifier.clickable { onAction(OtpAction.OnEditPhoneClicked) },
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = EchoTheme.colorScheme.surface.onColor,
                            textDecoration = TextDecoration.Underline,
                        )
                    ) { append("Change") }
                },
                style = EchoTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
        OtpInputField(
            digits = state.otpDigits,
            isError = state.otpError != null,
            onDigitChanged = { index, digit -> onAction(OtpAction.OnDigitChanged(index, digit)) },
            onPaste = { onAction(OtpAction.OnPaste(it)) },
            onBackspace = { onAction(OtpAction.OnBackspace(it)) },
            onFilled = { onAction(OtpAction.OnFilled) },
            isLoading = state.isLoading,
        )

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
        Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
        ResendButton(state = state, onAction = onAction)
    }
}

@Composable
private fun ResendButton(
    state: OtpScreenState,
    onAction: (OtpAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(EchoTheme.shapes.input)
            .background(EchoTheme.colorScheme.primary.container)
            .clickable(enabled = state.canResend) { onAction(OtpAction.OnResendClicked) }
            .padding(
                vertical = EchoTheme.spacing.padding.small,
                horizontal = EchoTheme.spacing.padding.medium,
            ),
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = if (state.canResend) EchoTheme.colorScheme.primary.onColor
            else EchoTheme.colorScheme.primary.dim,
            modifier = Modifier.size(EchoTheme.dimen.icon.small),
        )
        Text(
            text = if (state.canResend) "Resend code"
            else "Resend in ${state.resendCooldownSeconds}s",
            style = EchoTheme.typography.bodyMedium,
            color = if (state.canResend) EchoTheme.colorScheme.primary.onContainer
            else EchoTheme.colorScheme.primary.dim,
        )
    }
}