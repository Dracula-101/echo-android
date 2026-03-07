package com.application.echo.feature.auth.screens.login

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.ui.components.adaptive.AdaptiveLayout
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha20
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha70
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegisterScreen: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> onLoginSuccess()
                is LoginEvent.ShowSnackbar -> Unit
            }
        }
    }

    EchoScaffold {
        LoginContent(
            state = state,
            onAction = viewModel::trySendAction,
            onNavigateToRegisterScreen = onNavigateToRegisterScreen,
        )
    }
}

@Composable
private fun LoginContent(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
) {
    AdaptiveLayout(
        layoutContentRatio = 0.4f,
        firstContent = {
            AppInfo()
        },
        secondContent = {
            LoginForm(
                modifier = Modifier
                    .padding(horizontal = EchoTheme.spacing.padding.medium),
                state = state,
                onAction = onAction,
                onNavigateToRegisterScreen = onNavigateToRegisterScreen,
            )
        },
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
    )
}

@Composable
private fun AppInfo(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Image(
            modifier = Modifier
                .padding(EchoTheme.spacing.padding.medium)
                .size(80.dp),
            imageVector = ImageVector.vectorResource(
                id = R.drawable.ic_logo,
            ),
            contentDescription = "App Logo",
        )
        Text(
            "Login",
            style = EchoTheme.typography.headlineLarge,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraSmall))
        Text(
            "Welcome back!",
            style = EchoTheme.typography.bodyLarge,
            color = EchoTheme.colorScheme.inverse.surface,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraLarge))
    }
}

@Composable
private fun LoginForm(
    modifier: Modifier = Modifier,
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column {
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
            leading = { _ ->
                Icon(
                    Icons.Default.MailOutline,
                    contentDescription = "Email Icon",
                )
            },
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
            leading = { _ ->
                Icon(
                    Icons.Default.Password,
                    contentDescription = "Password Icon",
                )
            },
            trailing = { _ ->
                IconButton(
                    modifier = Modifier.size(EchoTheme.dimen.icon.medium),
                    onClick = { onAction(LoginAction.OnTogglePasswordVisibility) }
                ) {
                    Icon(
                        imageVector = if (state.isPasswordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.RemoveRedEye
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
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.medium))
        if (state.generalError != null) {
            Text(
                text = state.generalError,
                style = EchoTheme.typography.bodyMedium,
                color = EchoTheme.colorScheme.secondary.color,
                modifier = modifier.padding(bottom = EchoTheme.spacing.padding.small),
            )
        }
        EchoFilledButton(
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
                "Login",
                style = EchoTheme.typography.titleLarge,
                color = EchoTheme.colorScheme.primary.onColor.copy(alpha = if (state.isLoading) 0.5f else 1f),
            )
            if (!state.isLoading) {
                Spacer(Modifier.size(EchoTheme.spacing.gap.small))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Login Icon",
                    tint = EchoTheme.colorScheme.primary.onColor,
                )
            }
        }
        HorizontalDivider(
            modifier = modifier
                .padding(vertical = EchoTheme.spacing.padding.large),
        )
        Row {
            OtherLoginOptions(
                modifier = modifier.weight(1f),
                onClick = {},
                icon = ImageVector.vectorResource(R.drawable.ic_google),
            )
            OtherLoginOptions(
                modifier = modifier.weight(1f),
                onClick = {},
                icon = ImageVector.vectorResource(R.drawable.ic_facebook),
            )
        }
        Spacer(modifier = modifier.height(EchoTheme.spacing.padding.extraLarge))
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                buildAnnotatedString {
                    append("Don't have an account? ")
                    val signUpText = "Sign Up"
                    append(signUpText)
                    addStyle(
                        style = EchoTheme.typography.bodyLarge.toSpanStyle().copy(
                            color = EchoTheme.colorScheme.primary.color,
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
                    ),
            )
            Text(
                "Forgot Password?",
                style = EchoTheme.typography.bodyLarge,
                color = EchoTheme.colorScheme.primary.color,
                textDecoration = TextDecoration.Underline,
                modifier = modifier
                    .clip(EchoTheme.shapes.snackbar)
                    .clickable { }
                    .padding(
                        vertical = EchoTheme.spacing.padding.extraSmall,
                        horizontal = EchoTheme.spacing.padding.small,
                    ),
            )
        }
    }
}

@Composable
private fun OtherLoginOptions(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(EchoTheme.colorScheme.primary.color.alpha20)
            .clickable { onClick() }
            .padding(vertical = EchoTheme.spacing.padding.small),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            imageVector = icon,
            contentDescription = "Other Login Options",
            modifier = Modifier.size(EchoTheme.dimen.icon.large),
            colorFilter = ColorFilter.tint(EchoTheme.colorScheme.primary.color.alpha70),
        )
    }
}
