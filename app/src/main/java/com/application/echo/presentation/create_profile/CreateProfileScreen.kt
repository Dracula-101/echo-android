package com.application.echo.presentation.create_profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.features.auth.model.PhoneInfo
import com.application.echo.ui.components.button.ButtonSize
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoTextButton
import com.application.echo.ui.components.common.EchoPill
import com.application.echo.ui.components.icon.EchoIconButton
import com.application.echo.ui.components.icon.EchoIconButtonSize
import com.application.echo.ui.components.pager.EchoLinePagerIndicator
import com.application.echo.ui.components.picker.EchoDatePickerField
import com.application.echo.ui.components.picker.toEchoDate
import com.application.echo.ui.components.picker.toMillis
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.components.snackbar.EchoSnackbarHost
import com.application.echo.ui.components.snackbar.rememberEchoSnackbarState
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha70
import com.application.echo.ui.design.utils.alpha90
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    viewModel: CreateProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarState = rememberEchoSnackbarState()
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is CreateProfileEvent.ShowSnackbar -> {
                    snackbarState.show(
                        message = it.message,
                        detail = it.detail,
                        code = it.code,
                    )
                }
            }
        }
    }

    EchoScaffold(
        topBar = {
            EchoProfileHeader(
                onPrevious = { viewModel.trySendAction(CreateProfileAction.OnPrevious) },
                currentPage = state.currentPage.index,
                totalPages = ProfileScreen.entries.size,
            )
        },
        snackbarHost = {
            EchoSnackbarHost(
                state = snackbarState,
                modifier = Modifier.imePadding()
            )
        },
        bottomBar = {
            EchoFilledButton(
                onClick = {
                    scope.launch {
                        keyboard?.hide()
                        delay(100)
                        viewModel.trySendAction(CreateProfileAction.OnContinueClick)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = EchoTheme.spacing.padding.medium)
                    .padding(bottom = EchoTheme.spacing.padding.medium),
                size = ButtonSize.Medium,
                isLoading = state.isLoading,
                enabled = !state.isLoading
            ) {
                Text(
                    when (state.currentPage) {
                        ProfileScreen.INFO -> "Next"
                        ProfileScreen.CUSTOMIZATION -> "Finish"
                    },
                    style = EchoTheme.typography.titleMedium,
                    color = EchoTheme.colorScheme.primary.onColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EchoTheme.spacing.padding.medium),
        ) {
            AnimatedContent(
                targetState = state.currentPage,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically(tween(700)) { it } + fadeIn(tween(700)) togetherWith
                                slideOutVertically(tween(300)) { -it } + fadeOut(tween(300))
                    } else {
                        slideInVertically(tween(700)) { -it } + fadeIn(tween(700)) togetherWith
                                slideOutVertically(tween(300)) { it } + fadeOut(tween(300))
                    }.using(SizeTransform(clip = false))
                },
                label = "OnboardingAnimatedContent"
            ) { page ->
                when (page) {
                    ProfileScreen.INFO -> AvatarInfo(
                        state = state,
                        onAction = { viewModel.trySendAction(it) },
                    )
                    ProfileScreen.CUSTOMIZATION -> HandleInfo(
                        state = state,
                        onAction = { viewModel.trySendAction(it) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun EchoProfileHeader(
    onPrevious: () -> Unit,
    currentPage: Int,
    totalPages: Int,
) {
    Column(
        modifier = Modifier.statusBarsPadding()
    ) {
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = EchoTheme.spacing.padding.large,
                    bottom = EchoTheme.spacing.padding.medium,
                    start = EchoTheme.spacing.padding.medium,
                    end = EchoTheme.spacing.padding.small
                ),
            contentAlignment = Alignment.Center
        ){
            EchoIconButton(
                onClick = onPrevious,
                icon = IconResource.Vector(Icons.AutoMirrored.Rounded.ArrowBackIos),
                size = EchoIconButtonSize.Medium,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                "STEP ${currentPage} OF ${totalPages}",
                style = EchoTheme.typography.bodyMedium,
                color = EchoTheme.colorScheme.surface.onColor.alpha70,
            )
        }
        EchoLinePagerIndicator(
            currentPage = currentPage - 1,
            totalSteps = totalPages,
            modifier = Modifier.padding(horizontal = EchoTheme.spacing.padding.medium)
        )
    }
}

