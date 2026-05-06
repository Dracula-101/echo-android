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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.ui.components.button.ButtonSize
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.checkbox.CheckboxSize
import com.application.echo.ui.components.checkbox.EchoCheckbox
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha10
import com.application.echo.ui.design.utils.alpha50
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
        },
        bottomBar = {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center,
            ){
                Text(
                    buildAnnotatedString {
                        withStyle(
                            EchoTheme.typography.bodyLarge
                                .toSpanStyle()
                                .copy(color = EchoTheme.colorScheme.surface.onColor.alpha50)
                        ) {
                            append("New here? ")
                        }
                        withStyle(
                            EchoTheme.typography.bodyLarge
                                .toSpanStyle()
                                .copy(color = EchoTheme.colorScheme.surface.onColor.alpha90)
                        ) {
                            append("Create an account")
                        }
                    },
                    style = EchoTheme.typography.bodyLarge,
                    color = EchoTheme.colorScheme.inverse.surface,
                    modifier = Modifier
                        .clip(EchoTheme.shapes.snackbar)
                        .clickable { onNavigateToRegisterScreen() }
                        .padding(
                            vertical = EchoTheme.spacing.padding.extraSmall,
                            horizontal = EchoTheme.spacing.padding.small,
                        )
                )
            }
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
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(EchoTheme.spacing.padding.medium)
            .padding(vertical = EchoTheme.spacing.padding.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            AppHeader()
        }
        item {
            Spacer(modifier = Modifier.padding(EchoTheme.spacing.gap.large))
        }
        item {
            LoginForm(
                state = state,
                onAction = onAction,
                onNavigateToRegisterScreen = onNavigateToRegisterScreen,
                onNavigateToPhoneAuthScreen = onNavigateToPhoneAuthScreen,
            )
        }
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
                .size(EchoTheme.dimen.icon.large),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_logo),
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
    val rememberMeValue = remember { mutableStateOf(false) }
    Column {
        Text(
            buildAnnotatedString {
                append("Welcome ")
                withStyle(
                    EchoTheme.typography.headlineMedium
                        .toSpanStyle()
                        .copy(color = EchoTheme.colorScheme.primary.color)
                ) {
                    append("back")
                }
            },
            style = EchoTheme.typography.headlineMedium,
            color = EchoTheme.colorScheme.surface.onColor,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.small))
        Text(
            "Sign in to the quiet part of your internet.",
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.surface.onColor.alpha50,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraLarge))
        EchoTextField(
            value = state.email,
            label = {
                Text(
                    "EMAIL",
                    style = EchoTheme.typography.labelSmall,
                    color = EchoTheme.colorScheme.surface.onColor.alpha50,
                    letterSpacing = 1.25.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            },
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
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.large))
        EchoTextField(
            value = state.password,
            label = {
                Text(
                    "PASSWORD",
                    style = EchoTheme.typography.labelSmall,
                    color = EchoTheme.colorScheme.surface.onColor.alpha50,
                    letterSpacing = 1.25.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            },
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
            labelTrailing = {
                Text(
                    "Forgot?",
                    style = EchoTheme.typography.bodySmall,

                    color = EchoTheme.colorScheme.primary.color,
                    modifier = Modifier
                        .clickable { /* TODO: Handle forgot password */ }
                        .padding(end = EchoTheme.spacing.padding.small),
                )
            },
            modifier = modifier,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.medium))
        Row {
            EchoCheckbox(
                checked = { rememberMeValue.value },
                onCheckedChange = {
                    rememberMeValue.value = it
                },
                enabled = !state.isLoading,
                size = CheckboxSize.Medium,
                modifier = Modifier.size(EchoTheme.dimen.icon.small)
            )
            Spacer(modifier = Modifier.size(EchoTheme.spacing.gap.extraSmall))
            Text(
                "Keep me signed in on this device",
                style = EchoTheme.typography.bodyMedium,
                color = EchoTheme.colorScheme.surface.onColor.alpha70,
                modifier = Modifier.padding(start = EchoTheme.spacing.gap.extraSmall),
            )
        }
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.large))
        EchoFilledButton(
            onClick = {
                focusManager.clearFocus()
                onAction(LoginAction.OnLoginClicked)
            },
            enabled = !state.isLoading,
            isLoading = state.isLoading,
            modifier = modifier.fillMaxWidth(),
            size = ButtonSize.Large,
        ) {
            Text(
                "Sign In",
                style = EchoTheme.typography.bodyLarge,
                color = EchoTheme.colorScheme.primary.onColor,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Sign In",
                tint = EchoTheme.colorScheme.primary.onColor,
                modifier = Modifier.size(EchoTheme.dimen.icon.small),
            )
        }
        Spacer(modifier = modifier.size(EchoTheme.spacing.gap.medium))
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            HorizontalDivider(
                modifier = modifier
                    .padding(vertical = EchoTheme.spacing.padding.large),
            )
            Text(
                "OR CONTINUE WITH",
                style = EchoTheme.typography.labelSmall,
                color = EchoTheme.colorScheme.surface.onColor.alpha50,
                letterSpacing = 1.25.sp,
                modifier = Modifier
                    .background(EchoTheme.colorScheme.background.color)
                    .padding(horizontal = EchoTheme.spacing.padding.medium),
            )
        }
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.medium),
        ) {
            OtherLoginOptions(
                text = "Continue with Phone",
                onClick = onNavigateToPhoneAuthScreen,
                icon = Icons.Outlined.Phone,
            )
            OtherLoginOptions(
                text = "Continue with Google",
                onClick = {},
                icon = ImageVector.vectorResource(R.drawable.ic_google),
                isActive = true,
            )
        }
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
            .fillMaxWidth()
            .clip(EchoTheme.shapes.input)
            .background(
                if (isActive) EchoTheme.colorScheme.background.onColor.alpha90
                else EchoTheme.colorScheme.background.onColor.alpha10
            )
            .border(
                width = 1.dp,
                color = EchoTheme.colorScheme.surface.onColor.alpha10,
                shape = EchoTheme.shapes.input,
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
                colorFilter = if (isActive) null else ColorFilter.tint(EchoTheme.colorScheme.surface.onColor),
            )
            Text(
                text,
                style = EchoTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) EchoTheme.colorScheme.surface.color else EchoTheme.colorScheme.surface.onColor,
                modifier = Modifier.padding(start = EchoTheme.spacing.padding.small),
            )
        }
    }
}
