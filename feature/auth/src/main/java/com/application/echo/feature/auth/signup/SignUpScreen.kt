package com.application.echo.feature.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoTextButton
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Sign-up screen entry-point.
 */
@Composable
fun SignUpScreen(
    onBackToLoginClick: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignUpEvent.SignUpSuccess -> { /* Auth state handles navigation */ }
                is SignUpEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    SignUpContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onDisplayNameChanged = { viewModel.trySendAction(SignUpAction.DisplayNameChanged(it)) },
        onEmailChanged = { viewModel.trySendAction(SignUpAction.EmailChanged(it)) },
        onPasswordChanged = { viewModel.trySendAction(SignUpAction.PasswordChanged(it)) },
        onConfirmPasswordChanged = { viewModel.trySendAction(SignUpAction.ConfirmPasswordChanged(it)) },
        onSignUpClick = { viewModel.trySendAction(SignUpAction.SignUpClicked) },
        onBackToLoginClick = onBackToLoginClick,
    )
}

@Composable
private fun SignUpContent(
    state: SignUpState,
    snackbarHostState: SnackbarHostState,
    onDisplayNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EchoTheme.colorScheme.surface.color),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // ── Header ──
            Text(
                text = "Create Account",
                style = EchoTheme.typography.headlineLarge,
                color = EchoTheme.colorScheme.surface.onColor,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join the Echo community",
                style = EchoTheme.typography.bodyLarge,
                color = EchoTheme.colorScheme.surface.variant,
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ── Form Card ──
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = EchoTheme.shapes.card,
                color = EchoTheme.colorScheme.surface.high,
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Display Name
                    OutlinedTextField(
                        value = state.displayName,
                        onValueChange = onDisplayNameChanged,
                        label = { Text("Display Name") },
                        placeholder = { Text("John Doe") },
                        singleLine = true,
                        isError = state.displayNameError != null,
                        supportingText = state.displayNameError?.let { error ->
                            { Text(text = error, color = EchoTheme.colorScheme.error.color) }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                        colors = echoTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = onEmailChanged,
                        label = { Text("Email") },
                        placeholder = { Text("you@example.com") },
                        singleLine = true,
                        isError = state.emailError != null,
                        supportingText = state.emailError?.let { error ->
                            { Text(text = error, color = EchoTheme.colorScheme.error.color) }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                        colors = echoTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = onPasswordChanged,
                        label = { Text("Password") },
                        singleLine = true,
                        isError = state.passwordError != null,
                        supportingText = state.passwordError?.let { error ->
                            { Text(text = error, color = EchoTheme.colorScheme.error.color) }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                        colors = echoTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = onConfirmPasswordChanged,
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        isError = state.confirmPasswordError != null,
                        supportingText = state.confirmPasswordError?.let { error ->
                            { Text(text = error, color = EchoTheme.colorScheme.error.color) }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onSignUpClick()
                            },
                        ),
                        colors = echoTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // General error
                    if (state.generalError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.generalError,
                            style = EchoTheme.typography.bodySmall,
                            color = EchoTheme.colorScheme.error.color,
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sign-up button
                    EchoFilledButton(
                        onClick = onSignUpClick,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (state.isLoading) "Creating account…" else "Create Account")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Back to login CTA ──
            EchoTextButton(onClick = onBackToLoginClick) {
                Text(
                    text = "Already have an account? Sign In",
                    style = EchoTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun echoTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EchoTheme.colorScheme.primary.color,
    unfocusedBorderColor = EchoTheme.colorScheme.outline.color,
    errorBorderColor = EchoTheme.colorScheme.error.color,
    focusedLabelColor = EchoTheme.colorScheme.primary.color,
    unfocusedLabelColor = EchoTheme.colorScheme.surface.variant,
    cursorColor = EchoTheme.colorScheme.primary.color,
)
