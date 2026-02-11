package com.application.echo.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.application.echo.ui.components.checkbox.EchoCheckbox
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Login screen entry-point.
 *
 * Obtains its [LoginViewModel] via Hilt and maps state + events to the UI.
 */
@Composable
fun LoginScreen(
    onSignUpClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-shot events
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> { /* Auth state handles navigation */ }
                is LoginEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LoginContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onEmailChanged = { viewModel.trySendAction(LoginAction.EmailChanged(it)) },
        onPasswordChanged = { viewModel.trySendAction(LoginAction.PasswordChanged(it)) },
        onLoginClick = { viewModel.trySendAction(LoginAction.LoginClicked) },
        onSignUpClick = onSignUpClick,
    )
}

@Composable
private fun LoginContent(
    state: LoginState,
    snackbarHostState: SnackbarHostState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isChecked = remember { mutableStateOf(false) }
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
            Spacer(modifier = Modifier.height(80.dp))

            // ── Header ──
            Text(
                text = "Welcome to Echo",
                style = EchoTheme.typography.headlineLarge,
                color = EchoTheme.colorScheme.surface.onColor,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to continue",
                style = EchoTheme.typography.bodyLarge,
                color = EchoTheme.colorScheme.surface.variant,
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Form Card ──
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = EchoTheme.shapes.card,
                color = EchoTheme.colorScheme.surface.high,
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

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
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onLoginClick()
                            },
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // Terms and condition
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        EchoCheckbox(
                            checked = { isChecked.value },
                            onCheckedChange = { value ->
                                isChecked.value = value
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Remember me",
                            style = EchoTheme.typography.bodyMedium,
                        )
                    }

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

                    // Sign-in button
                    EchoFilledButton(
                        onClick = onLoginClick,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (state.isLoading) "Signing in…" else "Sign In")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Sign-up CTA ──
            EchoTextButton(onClick = onSignUpClick) {
                Text(
                    text = "Don't have an account? Sign Up",
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

