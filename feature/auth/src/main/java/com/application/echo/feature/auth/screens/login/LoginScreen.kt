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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.adaptive.AdaptiveLayout
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha10
import com.application.echo.ui.design.utils.alpha20
import com.application.echo.ui.design.utils.alpha70

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegisterScreen: () -> Unit
) {
    EchoScaffold{
        LoginContent(
            onNavigateToRegisterScreen = onNavigateToRegisterScreen
        )
    }
}

@Composable
private fun LoginContent(onNavigateToRegisterScreen: () -> Unit) {
    AdaptiveLayout(
        layoutContentRatio = 0.4f,
        firstContent = {
            AppInfo()
        },
        secondContent = {
            LoginForm(
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
            "Login",
            style = EchoTheme.typography.headlineLarge,
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraSmall))
        Text(
            "Welcome back!",
            style = EchoTheme.typography.bodyLarge,
            color = EchoTheme.colorScheme.inverse.surface
        )
        Spacer(modifier = modifier.size(EchoTheme.spacing.padding.extraLarge))
    }
}

@Composable
private fun LoginForm(
    modifier: Modifier = Modifier,
    onNavigateToRegisterScreen: () -> Unit
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
                placeholder = "Enter your password",
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
                    "Login",
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
            Row {
                OtherLoginOptions(
                    modifier = modifier
                        .weight(1f),
                    onClick = {},
                    icon = ImageVector.vectorResource(R.drawable.ic_google)
                )
                OtherLoginOptions(
                    modifier = modifier
                        .weight(1f),
                    onClick = {},
                    icon = ImageVector.vectorResource(R.drawable.ic_facebook)
                )
            }
            Spacer(modifier = modifier.height(EchoTheme.spacing.padding.extraLarge))
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
                        append("Don't have an account? ")
                        val signUpText = "Sign Up"
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
                        .clickable {
                            onNavigateToRegisterScreen()
                        }
                        .padding(
                            vertical = EchoTheme.spacing.padding.extraSmall,
                            horizontal = EchoTheme.spacing.padding.small
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
                            horizontal = EchoTheme.spacing.padding.small
                        ),
                )
            }
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
            .clickable{ onClick() }
            .padding(vertical = EchoTheme.spacing.padding.small),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            imageVector = icon,
            contentDescription = "Other Login Options",
            modifier = Modifier
                .size(EchoTheme.dimen.icon.large),
            colorFilter = ColorFilter.tint(EchoTheme.colorScheme.primary.color.alpha70)
        )
    }
}