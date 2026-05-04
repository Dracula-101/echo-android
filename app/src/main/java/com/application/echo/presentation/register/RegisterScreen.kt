package com.application.echo.presentation.register

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha90
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLoginScreen: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarState = rememberEchoSnackbarState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RegisterEvent.ShowSnackbar -> {
                    snackbarState.show(
                        message = event.message,
                        type = event.type,
                        detail = event.detail,
                        code = event.code,
                    )
                }
            }
        }
    }

    EchoScaffold(
        snackbarHost = {
            EchoSnackbarHost(
                state = snackbarState,
            )
        }
    ) {
        RegisterContent(
            state = state,
            onAction = viewModel::trySendAction,
            onNavigateToLoginScreen = onNavigateToLoginScreen,
        )
    }
}

@Composable
private fun RegisterContent(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(EchoTheme.spacing.padding.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.padding(EchoTheme.spacing.gap.extraLarge))
        AppHeader()
        Spacer(modifier = Modifier.padding(EchoTheme.spacing.gap.extraLarge))
        RegisterForm(
            state = state,
            onAction = onAction,
            onNavigateToLoginScreen = onNavigateToLoginScreen,
        )
    }
}

@Composable
private fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(EchoTheme.dimen.icon.extraLarge),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_app_logo_grayscale),
            contentDescription = "App Logo",
        )
        Text(
            "echo",
            style = EchoTheme.typography.titleLarge,
        )
    }
}

@Composable
private fun RegisterForm(
    modifier: Modifier = Modifier,
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column {
        Text(
            "Create an account",
            style = EchoTheme.typography.headlineMedium,
            color = EchoTheme.colorScheme.surface.onColor,
        )
        Text(
            "Join us and start your echo journey today!",
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.surface.onColor.alpha50,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraLarge))
        EchoTextField(
            value = state.phoneNumber,
            label = "Phone",
            onValueChange = { onAction(RegisterAction.OnPhoneNumberChanged(it)) },
            placeholder = "Enter your phone number",
            isError = state.phoneNumberError != null,
            errorText = state.phoneNumberError,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            modifier = modifier,
        )
        HorizontalDivider(
            modifier = modifier
                .padding(vertical = EchoTheme.spacing.padding.large),
        )
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                buildAnnotatedString {
                    append("Already have an account? ")
                    addStyle(
                        style = EchoTheme.typography.bodyLarge.toSpanStyle().copy(
                            color = EchoTheme.colorScheme.surface.onColor.alpha50,
                        ),
                        start = 0,
                        end = length,
                    )
                    val loginText = "Login"
                    append(loginText)
                    addStyle(
                        style = EchoTheme.typography.bodyLarge.toSpanStyle().copy(
                            color = EchoTheme.colorScheme.surface.onColor.alpha90,
                        ),
                        start = length - loginText.length,
                        end = length,
                    )
                },
                style = EchoTheme.typography.bodyLarge,
                color = EchoTheme.colorScheme.inverse.surface,
                modifier = modifier
                    .clip(EchoTheme.shapes.snackbar)
                    .clickable { onNavigateToLoginScreen() }
                    .padding(
                        vertical = EchoTheme.spacing.padding.extraSmall,
                        horizontal = EchoTheme.spacing.padding.small,
                    ),
            )
        }
    }
}
