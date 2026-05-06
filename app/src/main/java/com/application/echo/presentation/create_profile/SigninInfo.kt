package com.application.echo.presentation.create_profile


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha90
import kotlinx.coroutines.launch

@Composable
internal fun SigninInfo(
    state: CreateProfileState,
    listState: LazyListState,
    onAction: (CreateProfileAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
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
                    onAction(CreateProfileAction.OnEmailChanged(it))
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
                    onAction(CreateProfileAction.OnPasswordChanged(it))
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
                            onAction(CreateProfileAction.OnChangePasswordVisibility)
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
