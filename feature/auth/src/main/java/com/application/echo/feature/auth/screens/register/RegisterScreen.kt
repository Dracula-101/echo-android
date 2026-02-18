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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.adaptive.AdaptiveLayout
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToRegisterScreen: () -> Unit,
) {
    EchoScaffold{
        RegisterContent(
            onNavigateToRegisterScreen = onNavigateToRegisterScreen
        )
    }
}

@Composable
private fun RegisterContent(
    onNavigateToRegisterScreen: () -> Unit,
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
                onNavigateToRegisterScreen = onNavigateToRegisterScreen
            )
        },
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    )
}

@Composable
private fun AppInfo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(
            modifier = Modifier
                .padding(EchoTheme.spacing.padding.medium)
                .size(80.dp),
            imageVector = ImageVector.vectorResource(
                id = R.drawable.ic_logo
            ),
            contentDescription = "App Logo"
        )
        Text(
            "Register",
            style = EchoTheme.typography.headlineLarge,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraSmall))
        Text(
            "Create an account to get started",
            style = EchoTheme.typography.bodyLarge,
            color = EchoTheme.colorScheme.inverse.surface
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraLarge))
    }
}

@Composable
private fun RegisterForm(
    modifier: Modifier = Modifier,
    onNavigateToRegisterScreen: () -> Unit,
) {
    LazyColumn {
        item {
            EchoTextField(
                value = "",
                label = "Email",
                onValueChange = {},
                placeholder = "Enter your email",
                leading = { isFocused ->
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
                value = "",
                label = "Password",
                onValueChange = {},
                placeholder = "Enter password",
                leading = { isFocused ->
                    Icon(
                        Icons.Default.Password,
                        contentDescription = "Password Icon",
                    )
                },
                trailing = { isFocused ->
                    Icon(
                        Icons.Default.RemoveRedEye,
                        contentDescription = "Show Password",
                    )
                },
                modifier = modifier,
            )
            Spacer(modifier = modifier.size(EchoTheme.spacing.padding.large))
        }
        item {
            EchoTextField(
                value = "",
                label = "Confirm Password",
                onValueChange = {},
                placeholder = "Enter password",
                leading = { isFocused ->
                    Icon(
                        Icons.Default.Password,
                        contentDescription = "Password Icon",
                    )
                },
                trailing = { isFocused ->
                    Icon(
                        Icons.Default.RemoveRedEye,
                        contentDescription = "Show Password",
                    )
                },
                modifier = modifier,
            )
            Spacer(modifier = modifier.size(EchoTheme.spacing.padding.large))
        }
        item {
            EchoFilledButton(
                onClick = {},
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text(
                    "Register",
                    style = EchoTheme.typography.titleLarge,
                )
                Spacer(Modifier.size(EchoTheme.spacing.gap.small))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Login Icon",
                    tint = EchoTheme.colorScheme.primary.onColor
                )
            }
            HorizontalDivider(
                modifier = modifier
                    .padding(vertical = EchoTheme.spacing.padding.large),
            )
        }
        item {
            Column(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    buildAnnotatedString {
                        append("Already have an account? ")
                        val signUpText = "Login"
                        append(signUpText)
                        addStyle(
                            style = EchoTheme.typography.bodyLarge.toSpanStyle().copy(
                                color = EchoTheme.colorScheme.primary.color
                            ),
                            start = length - signUpText.length,
                            end = length
                        )
                    },
                    style = EchoTheme.typography.bodyLarge,
                    color = EchoTheme.colorScheme.inverse.surface,
                    modifier = modifier
                        .clip(EchoTheme.shapes.snackbar)
                        .clickable { onNavigateToRegisterScreen() }
                        .padding(
                            vertical = EchoTheme.spacing.padding.extraSmall,
                            horizontal = EchoTheme.spacing.padding.small
                        ),
                )
            }
        }
    }
}