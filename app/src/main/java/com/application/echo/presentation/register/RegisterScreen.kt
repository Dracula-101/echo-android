package com.application.echo.presentation.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.features.auth.model.PhoneInfo
import com.application.echo.presentation.create_profile.CreateProfileAction
import com.application.echo.ui.components.button.ButtonSize
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha70
import com.application.echo.ui.design.utils.alpha90
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLoginScreen: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarState = rememberEchoSnackbarState()
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
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
        },
        topBar = {
            AppHeader()
        },
        bottomBar = {
            EchoFilledButton(
                onClick = {
                    scope.launch {
                        keyboard?.hide()
                        delay(100)
                        viewModel.trySendAction(RegisterAction.OnRegisterClick)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = EchoTheme.spacing.padding.medium)
                    .padding(bottom = EchoTheme.spacing.padding.medium),
                size = ButtonSize.Medium,
                isLoading = state.isLoading,
                enabled = !state.isLoading,
            ) {
                Text(
                    "Register",
                    style = EchoTheme.typography.titleMedium,
                    color = EchoTheme.colorScheme.primary.onColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    ) {
        RegisterContent(
            state = state,
            onAction = viewModel::trySendAction,
        )
    }
}

@Composable
private fun RegisterContent(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = EchoTheme.spacing.padding.medium)
    ) {
        if (state.phoneInfo != null) {
            PhoneInfoChip(phoneInfo = state.phoneInfo)
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
        ) {
            item {
                Text(
                    buildAnnotatedString {
                        append("Set up ")
                        withStyle(
                            EchoTheme.typography.headlineMedium
                                .toSpanStyle()
                                .copy(
                                    color = EchoTheme.colorScheme.primary.color,
                                    fontWeight = FontWeight.ExtraBold
                                )
                        ) {
                            append("sign-in")
                        }
                    },
                    style = EchoTheme.typography.headlineMedium,
                    color = EchoTheme.colorScheme.surface.onColor,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.small))
                Text(
                    "Add an email and password so you can get back in from any device.",
                    style = EchoTheme.typography.bodyMedium,
                    color = EchoTheme.colorScheme.surface.onColor.alpha50,
                )
                Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
            }
            item {
                EchoTextField(
                    value = state.email ?: "",
                    onValueChange = {
                        onAction(RegisterAction.OnEmailChanged(it))
                    },
                    label = {
                        Text(
                            "EMAIL",
                            style = EchoTheme.typography.labelSmall,
                            letterSpacing = 1.25.sp,
                            fontWeight = FontWeight.Medium,
                            color = EchoTheme.colorScheme.surface.onColor.alpha90,
                        )
                    },
                    placeholder = "Enter your email address",
                    errorText = state.emailError,
                    isError = state.emailError != null,
                    borderColor = if (state.isValidEmail) {
                        EchoTheme.colorScheme.success.color
                    } else null,
                    trailing = {
                        if (state.isValidEmail) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        EchoTheme.colorScheme.success.color,
                                        shape = RoundedCornerShape(50)
                                    ).padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = EchoTheme.colorScheme.success.onColor,
                                    modifier = Modifier.size(EchoTheme.dimen.icon.small)
                                )
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
            }
            item {
                EchoTextField(
                    value = state.password ?: "",
                    onValueChange = {
                        onAction(RegisterAction.OnPasswordChanged(it))
                    },
                    label = {
                        Text(
                            "PASSWORD",
                            style = EchoTheme.typography.labelSmall,
                            letterSpacing = 1.25.sp,
                            fontWeight = FontWeight.Medium,
                            color = EchoTheme.colorScheme.surface.onColor.alpha90,
                        )
                    },
                    placeholder = "Create a password",
                    errorText = state.passwordError,
                    isError = state.passwordError != null,
                    trailing = {
                        Text(
                            if (state.isPasswordVisible) "Hide" else "Show",
                            style = EchoTheme.typography.labelSmall,
                            modifier = Modifier.clickable {
                                onAction(RegisterAction.OnChangePasswordVisibility)
                            },
                        )
                    },
                    visualTransformation = if (state.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            scope.launch {
                                listState.animateScrollToItem(
                                    index = listState.layoutInfo.totalItemsCount - 1
                                )
                            }
                        }
                    )
                )
                state.password?.let {
                    PasswordStrengthIndicator(
                        password = it,
                        passwordStrength = state.passwordStrength,
                        modifier = Modifier.padding(top = EchoTheme.spacing.padding.small)
                    )
                }
                Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
            }
            item {
                Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
            }
        }
    }
}


@Composable
private fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                horizontal = EchoTheme.spacing.padding.medium,
                vertical = EchoTheme.spacing.padding.large
            ),
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
internal fun PhoneInfoChip(
    phoneInfo: PhoneInfo,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = EchoTheme.spacing.padding.small)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(EchoTheme.colorScheme.success.container.alpha70)
                .padding(
                    horizontal = EchoTheme.spacing.padding.small,
                    vertical = EchoTheme.spacing.padding.extraSmall
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = EchoTheme.colorScheme.success.onContainer,
                modifier = Modifier.size(EchoTheme.dimen.icon.extraSmall)
            )
            Spacer(modifier = Modifier.width(EchoTheme.spacing.gap.extraSmall))
            Text(
                text = "${phoneInfo.toDisplayString()} verified",
                style = EchoTheme.typography.labelMedium,
                color = EchoTheme.colorScheme.success.onContainer,
            )
            Spacer(modifier = Modifier.width(EchoTheme.spacing.gap.extraSmall))
        }
    }
}
