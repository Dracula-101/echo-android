package com.application.echo.feature.auth.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.ui.components.adaptive.AdaptiveLayout
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLoginScreen: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RegisterEvent.RegisterSuccess -> onRegisterSuccess()
                is RegisterEvent.ShowSnackbar -> Unit
            }
        }
    }

    EchoScaffold {
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
    AdaptiveLayout(
        layoutContentRatio = 0.4f,
        firstContent = {
            AppInfo()
        },
        secondContent = {
            RegisterForm(
                modifier = Modifier
                    .padding(horizontal = EchoTheme.spacing.padding.medium),
                state = state,
                onAction = onAction,
                onNavigateToLoginScreen = onNavigateToLoginScreen,
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
            "Register",
            style = EchoTheme.typography.headlineLarge,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraSmall))
        Text(
            "Create an account to get started",
            style = EchoTheme.typography.bodyLarge,
            color = EchoTheme.colorScheme.inverse.surface,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraLarge))
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

    LazyColumn {
        item {
            EchoTextField(
                value = state.email,
                label = "Email",
                onValueChange = { onAction(RegisterAction.OnEmailChanged(it)) },
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
        }
        item {
            EchoTextField(
                value = state.password,
                label = "Password",
                onValueChange = { onAction(RegisterAction.OnPasswordChanged(it)) },
                placeholder = "Enter password",
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
                    imeAction = ImeAction.Next,
                ),
                leading = { _ ->
                    Icon(
                        Icons.Default.Password,
                        contentDescription = "Password Icon",
                    )
                },
                trailing = { _ ->
                    IconButton(onClick = { onAction(RegisterAction.OnTogglePasswordVisibility) }) {
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
        }
        item {
            EchoTextField(
                value = state.confirmPassword,
                label = "Confirm Password",
                onValueChange = { onAction(RegisterAction.OnConfirmPasswordChanged(it)) },
                placeholder = "Confirm your password",
                isError = state.confirmPasswordError != null,
                errorText = state.confirmPasswordError,
                enabled = !state.isLoading,
                visualTransformation = if (state.isConfirmPasswordVisible) {
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
                        onAction(RegisterAction.OnRegisterClicked)
                    },
                ),
                leading = { _ ->
                    Icon(
                        Icons.Default.Password,
                        contentDescription = "Password Icon",
                    )
                },
                trailing = { _ ->
                    IconButton(onClick = { onAction(RegisterAction.OnToggleConfirmPasswordVisibility) }) {
                        Icon(
                            imageVector = if (state.isConfirmPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.RemoveRedEye
                            },
                            contentDescription = if (state.isConfirmPasswordVisible) {
                                "Hide Password"
                            } else {
                                "Show Password"
                            },
                        )
                    }
                },
                modifier = modifier,
            )
            Spacer(modifier = modifier.size(EchoTheme.spacing.padding.large))
        }
        if (state.generalError != null) {
            item {
                Text(
                    text = state.generalError,
                    style = EchoTheme.typography.bodyMedium,
                    color = EchoTheme.colorScheme.error.color,
                    modifier = modifier.padding(bottom = EchoTheme.spacing.padding.medium),
                )
            }
        }
        item {
            EchoFilledButton(
                onClick = {
                    focusManager.clearFocus()
                    onAction(RegisterAction.OnRegisterClicked)
                },
                enabled = !state.isLoading,
                modifier = modifier.fillMaxWidth(),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(EchoTheme.dimen.icon.small),
                        strokeWidth = 2.dp,
                        color = EchoTheme.colorScheme.primary.onColor,
                    )
                    Spacer(Modifier.size(EchoTheme.spacing.gap.small))
                }
                Text(
                    "Register",
                    style = EchoTheme.typography.titleLarge,
                )
                if (!state.isLoading) {
                    Spacer(Modifier.size(EchoTheme.spacing.gap.small))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Register Icon",
                        tint = EchoTheme.colorScheme.primary.onColor,
                    )
                }
            }
            HorizontalDivider(
                modifier = modifier
                    .padding(vertical = EchoTheme.spacing.padding.large),
            )
        }
        item {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    buildAnnotatedString {
                        append("Already have an account? ")
                        val loginText = "Login"
                        append(loginText)
                        addStyle(
                            style = EchoTheme.typography.bodyLarge.toSpanStyle().copy(
                                color = EchoTheme.colorScheme.primary.color,
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
}
