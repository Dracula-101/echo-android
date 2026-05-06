package com.application.echo.presentation.phone

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.ui.components.button.ButtonSize
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoTextButton
import com.application.echo.ui.components.flags.FlagDrawable
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.R
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha70

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun PhoneAuthScreen(
    onNavigateToEmailAuth: () -> Unit,
    viewModel: PhoneAuthViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = rememberEchoSnackbarState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is PhoneAuthEvent.ShowSnackbar -> snackbarHostState.show(
                    message = event.message,
                    detail = event.detail,
                    code = event.code,
                    type = event.type,
                )
            }
        }
    }

    Scaffold(
        bottomBar = {
            Text(
                buildAnnotatedString {
                    append("By continuing, you agree to our ")
                    append("Terms")
                    addStyle(
                        style = EchoTheme.typography.labelMedium.toSpanStyle().copy(
                            color = EchoTheme.colorScheme.primary.onColor.alpha70,
                            textDecoration = TextDecoration.Underline,
                        ),
                        start = length - "Terms".length,
                        end = length,
                    )
                    append(" and ")
                    append("Privacy Policy")
                    addStyle(
                        style = EchoTheme.typography.labelMedium.toSpanStyle().copy(
                            color = EchoTheme.colorScheme.primary.onColor.alpha70,
                            textDecoration = TextDecoration.Underline,
                        ),
                        start = length - "Privacy Policy".length,
                        end = length,
                    )
                },
                style = EchoTheme.typography.labelMedium,
                color = EchoTheme.colorScheme.scrim.color,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = EchoTheme.spacing.padding.medium),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    ) { _ ->
        PhoneNumberContent(
            state = state,
            countries = viewModel.countries,
            onAction = viewModel::trySendAction,
            onNavigateToEmailAuth = onNavigateToEmailAuth,
        )
    }
}

@Composable
private fun PhoneNumberContent(
    state: PhoneAuthState,
    countries: List<UiCountry>,
    onAction: (PhoneAuthAction) -> Unit,
    onNavigateToEmailAuth: () -> Unit,
) {
    var isSheetVisible by remember { mutableStateOf(false) }
    val activity = LocalActivity.current
    val keyboard = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EchoTheme.colorScheme.background.color)
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(EchoTheme.spacing.padding.medium)
            .padding(vertical = EchoTheme.spacing.padding.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppHeader()
        Spacer(modifier = Modifier.padding(EchoTheme.spacing.gap.extraLarge))
        PhoneNumberForm(
            state = state,
            onCountryClick = { isSheetVisible = true },
            onAction = onAction,
        )
        Spacer(modifier = Modifier.weight(1f))
        EchoFilledButton(
            onClick = {
                if (!state.isLoading && activity != null) onAction(PhoneAuthAction.OnSendOtpClicked(activity))
                keyboard?.hide()
            },
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.isLoading,
            enabled = !state.isLoading,
            size = ButtonSize.Large,
        ) {
            Text(
                "Continue",
                style = EchoTheme.typography.bodyLarge,
                color = if (state.isLoading) EchoTheme.colorScheme.surface.onColor.alpha50
                else EchoTheme.colorScheme.surface.onColor,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = if (state.isLoading) EchoTheme.colorScheme.surface.onColor.alpha50 else EchoTheme.colorScheme.surface.onColor,
                modifier = Modifier.size(EchoTheme.dimen.icon.extraSmall)
            )
        }
        Spacer(modifier = Modifier.padding(EchoTheme.spacing.gap.small))
        EchoTextButton(
            onClick = onNavigateToEmailAuth,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Use email instead",
                style = EchoTheme.typography.bodyMedium,
                color = EchoTheme.colorScheme.surface.onColor.alpha70,
            )
        }
        Spacer(modifier = Modifier.padding(EchoTheme.spacing.gap.large))
    }

    if (isSheetVisible) {
        CountryPickerSheet(
            countries = countries,
            selected = state.selectedCountry,
            onSelected = {
                onAction(PhoneAuthAction.OnCountryChanged(it))
                isSheetVisible = false
            },
            onDismiss = {
                isSheetVisible = false
            },
        )
    }
}

@Composable
private fun AppHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(EchoTheme.dimen.icon.large),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_logo),
            contentDescription = "App Logo",
        )
        Text(
            "echo",
            style = EchoTheme.typography.titleLarge,
            color = EchoTheme.colorScheme.background.onColor,
        )
    }
}

@Composable
private fun PhoneNumberForm(
    state: PhoneAuthState,
    onCountryClick: () -> Unit,
    onAction: (PhoneAuthAction) -> Unit,
) {
    val activity = LocalActivity.current
    val keyboard = LocalSoftwareKeyboardController.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            buildAnnotatedString {
                append("What's your ")
                withStyle(
                    EchoTheme.typography.headlineMedium
                        .toSpanStyle()
                        .copy(color = EchoTheme.colorScheme.primary.color)
                ) {
                    append("number")
                }
                append("?")
            },
            style = EchoTheme.typography.headlineMedium,
            color = EchoTheme.colorScheme.background.onColor,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "We'll send you a code to verify. Your number is never shared without you.",
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.scrim.color,
        )
        Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.large))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.medium),
        ) {
            CountryCodeButton(
                country = state.selectedCountry,
                modifier = Modifier.weight(1f),
                onClick = onCountryClick,
            )
            EchoTextField(
                value = state.phoneNumber,
                onValueChange = { onAction(PhoneAuthAction.OnPhoneNumberChanged(it)) },
                modifier = Modifier.weight(2.5f),
                placeholder = "Phone number",
                isError = state.phoneNumberError != null,
                errorText = state.phoneNumberError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (activity != null) onAction(PhoneAuthAction.OnSendOtpClicked(activity))
                        keyboard?.hide()
                    },
                ),
            )
        }
    }
}

@Composable
private fun CountryCodeButton(
    country: UiCountry,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(EchoTheme.shapes.input)
            .border(
                width = 1.dp,
                color = EchoTheme.colorScheme.outline.color,
                shape = EchoTheme.shapes.input,
            )
            .background(color = EchoTheme.colorScheme.surface.high)
            .clickable(onClick = onClick)
            .padding(horizontal = EchoTheme.spacing.padding.small, vertical = EchoTheme.spacing.padding.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(FlagDrawable.forIso(country.isoCode)),
            contentDescription = country.name,
            modifier = Modifier
                .size(width = EchoTheme.dimen.icon.medium, height = EchoTheme.dimen.icon.medium)
                .clip(RoundedCornerShape(2.dp)),
        )
        Spacer(modifier = Modifier.width(EchoTheme.spacing.gap.small))
        Text(
            text = "+${country.dialCode}",
            style = EchoTheme.typography.bodyLarge,
            color = EchoTheme.colorScheme.surface.onColor,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryPickerSheet(
    countries: List<UiCountry>,
    selected: UiCountry,
    onSelected: (UiCountry) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filtered = remember(query) {
        if (query.isBlank()) countries
        else countries.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.isoCode.contains(query, ignoreCase = true) ||
                    it.dialCode.contains(query)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = EchoTheme.colorScheme.surface.color,
        scrimColor = EchoTheme.colorScheme.background.color.copy(alpha = 0.6f),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = EchoTheme.spacing.padding.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Select country",
                    style = EchoTheme.typography.titleMedium,
                    color = EchoTheme.colorScheme.surface.onColor,
                )
                Text(
                    "${filtered.size} countries",
                    style = EchoTheme.typography.labelSmall,
                    color = EchoTheme.colorScheme.scrim.color,
                )
            }
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.medium))
            EchoTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search by name, code or +dial",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = EchoTheme.spacing.padding.medium),
            )
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.medium))
            HorizontalDivider(color = EchoTheme.colorScheme.outline.color)
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "No results for \"$query\"",
                        style = EchoTheme.typography.bodyMedium,
                        color = EchoTheme.colorScheme.scrim.color,
                    )
                }
            } else {
                LazyColumn {
                    items(filtered, key = { it.isoCode }) { country ->
                        CountryPickerItem(
                            country = country,
                            isSelected = country.isoCode == selected.isoCode,
                            onClick = {
                                onSelected(country)
                                query = ""
                            },
                        )
                        HorizontalDivider(color = EchoTheme.colorScheme.outline.color)
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryPickerItem(
    country: UiCountry,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) EchoTheme.colorScheme.surface.variant
                else EchoTheme.colorScheme.surface.color,
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = EchoTheme.spacing.padding.medium,
                vertical = 14.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.medium),
    ) {
        Image(
            painter = painterResource(FlagDrawable.forIso(country.isoCode)),
            contentDescription = country.name,
            modifier = Modifier
                .size(width = 24.dp, height = 18.dp)
                .clip(RoundedCornerShape(2.dp))
        )
        Text(
            text = country.name,
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.surface.onColor,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
        Text(
            text = "+${country.dialCode}",
            style = EchoTheme.typography.bodyMedium,
            color = EchoTheme.colorScheme.scrim.color,
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = EchoTheme.colorScheme.primary.color,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}