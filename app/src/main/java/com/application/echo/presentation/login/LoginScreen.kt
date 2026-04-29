package com.application.echo.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoTextButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha10
import com.application.echo.ui.design.utils.alpha20
import com.application.echo.ui.design.utils.alpha30
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha60
import com.application.echo.ui.design.utils.alpha70
import com.application.echo.ui.design.utils.alpha90
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToPhoneAuthScreen: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarState = rememberEchoSnackbarState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is LoginEvent.ShowSnackbar -> snackbarState.show(
                    message = event.message,
                    code = event.code,
                    detail = event.detail,
                    type = event.type,
                )
            }
        }
    }

    EchoScaffold(
        snackbarHost = {
            EchoSnackbarHost(state = snackbarState)
        }
    ) {
        LoginContent(
            state = state,
            onAction = viewModel::trySendAction,
            onNavigateToRegisterScreen = onNavigateToRegisterScreen,
            onNavigateToPhoneAuthScreen = onNavigateToPhoneAuthScreen,
        )
    }
}

@Composable
private fun LoginContent(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToPhoneAuthScreen: () -> Unit,
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
        LoginForm(
            state = state,
            onAction = onAction,
            onNavigateToRegisterScreen = onNavigateToRegisterScreen,
            onNavigateToPhoneAuthScreen = onNavigateToPhoneAuthScreen,
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
private fun LoginForm(
    modifier: Modifier = Modifier,
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToPhoneAuthScreen: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Column {
        Text(
            "Welcome back",
            style = EchoTheme.typography.headlineMedium,
            color = EchoTheme.colorScheme.surface.onColor,
        )
        Text(
            "Sign in to the quiet part of the internet",
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.surface.onColor.alpha50,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraLarge))
        EchoTextField(
            value = state.email,
            label = "Email",
            onValueChange = { onAction(LoginAction.OnEmailChanged(it)) },
            placeholder = "Enter your email",
            isError = state.emailError != null,
            errorText = state.emailError,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            modifier = modifier,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.medium))
        EchoTextField(
            value = state.password,
            label = "Password",
            onValueChange = { onAction(LoginAction.OnPasswordChanged(it)) },
            placeholder = "Enter your password",
            isError = state.passwordError != null,
            errorText = state.passwordError,
            enabled = !state.isLoading,
            visualTransformation = if (state.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onAction(LoginAction.OnLoginClicked)
                },
            ),
            trailing = { _ ->
                IconButton(
                    modifier = Modifier.size(EchoTheme.dimen.icon.medium),
                    onClick = { onAction(LoginAction.OnTogglePasswordVisibility) }
                ) {
                    Icon(
                        imageVector = if (state.isPasswordVisible) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.RemoveRedEye
                        },
                        contentDescription = if (state.isPasswordVisible) {
                            "Hide Password"
                        } else {
                            "Show Password"
                        },
                    )
                }
            },
            modifier = modifier,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.small))
        Text(
            "Forgot Password?",
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.surface.onColor.alpha70,
            textDecoration = TextDecoration.Underline,
            modifier = modifier
                .clip(EchoTheme.shapes.snackbar)
                .clickable { }
                .padding(
                    vertical = EchoTheme.spacing.padding.extraSmall,
                    horizontal = EchoTheme.spacing.padding.small,
                )
                .align(Alignment.End),
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.large))
        EchoTextButton(
            onClick = {
                focusManager.clearFocus()
                onAction(LoginAction.OnLoginClicked)
            },
            enabled = !state.isLoading,
            modifier = modifier.fillMaxWidth(),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(EchoTheme.dimen.icon.small),
                    strokeWidth = 2.dp,
                    color = EchoTheme.colorScheme.primary.onColor.alpha50,
                )
                Spacer(Modifier.size(EchoTheme.spacing.gap.small))
            }
            Text(
                "Sign in",
                style = EchoTheme.typography.bodyLarge,
                color = EchoTheme.colorScheme.primary.onColor.copy(alpha = if (state.isLoading) 0.5f else 1f),
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            HorizontalDivider(
                modifier = modifier
                    .padding(vertical = EchoTheme.spacing.padding.large),
            )
            Text(
                "or continue with",
                style = EchoTheme.typography.bodyLarge,
                color = EchoTheme.colorScheme.surface.onColor.alpha50,
                modifier = Modifier
                    .background(EchoTheme.colorScheme.background.color)
                    .padding(horizontal = EchoTheme.spacing.padding.medium),
            )
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.medium),
        ) {
            OtherLoginOptions(
                modifier = modifier.weight(1f),
                text = "Phone",
                onClick = onNavigateToPhoneAuthScreen,
                icon = Icons.Default.Phone,
                isActive = true,
            )
            OtherLoginOptions(
                modifier = modifier.weight(1f),
                text = "Google",
                onClick = {},
                icon = ImageVector.vectorResource(R.drawable.ic_google),
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Text(
            buildAnnotatedString {
                append("New here? ")
                addStyle(
                    style = EchoTheme.typography.bodyLarge.toSpanStyle().copy(
                        color = EchoTheme.colorScheme.surface.onColor.alpha50,
                    ),
                    start = 0,
                    end = length,
                )
                val signUpText = "Create an account"
                append(signUpText)
                addStyle(
                    style = EchoTheme.typography.bodyLarge.toSpanStyle().copy(
                        color = EchoTheme.colorScheme.surface.onColor.alpha90,
                    ),
                    start = length - signUpText.length,
                    end = length,
                )
            },
            style = EchoTheme.typography.bodyLarge,
            color = EchoTheme.colorScheme.inverse.surface,
            modifier = modifier
                .clip(EchoTheme.shapes.snackbar)
                .clickable { onNavigateToRegisterScreen() }
                .padding(
                    vertical = EchoTheme.spacing.padding.extraSmall,
                    horizontal = EchoTheme.spacing.padding.small,
                )
        )
    }
}

@Composable
private fun OtherLoginOptions(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    icon: ImageVector,
    isActive: Boolean = false,
) {
    Box(
        modifier = modifier
            .clip(EchoTheme.shapes.input)
            .background(
                if (isActive) EchoTheme.colorScheme.background.onColor.alpha90
                else EchoTheme.colorScheme.background.onColor.alpha10
            )
            .clickable { onClick() }
            .padding(EchoTheme.spacing.padding.medium),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.extraSmall),
        ) {
            Image(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(EchoTheme.dimen.icon.medium),
                colorFilter = ColorFilter.tint(
                    if (isActive) EchoTheme.colorScheme.surface.color
                    else EchoTheme.colorScheme.surface.onColor.alpha50
                ),
            )
            Text(
                text,
                style = EchoTheme.typography.bodyLarge,
                color = if (isActive) EchoTheme.colorScheme.surface.color else EchoTheme.colorScheme.surface.onColor.alpha50,
                modifier = Modifier.padding(start = EchoTheme.spacing.padding.small),
            )
        }
    }
}
