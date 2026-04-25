package com.application.echo.presentation.rootnav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.core.navigation.EchoNavHost
import com.application.echo.core.navigation.Navigator
import com.application.echo.core.navigation.echoComposable
import com.application.echo.core.navigation.transition.EchoTransitionPreset
import com.application.echo.features.auth.model.AuthState
import com.application.echo.presentation.create_profile.CreateProfileScreen
import com.application.echo.presentation.conversation.ConversationScreen
import com.application.echo.presentation.login.LoginScreen
import com.application.echo.presentation.otp.OtpScreen
import com.application.echo.presentation.phone.PhoneAuthScreen
import com.application.echo.presentation.register.RegisterScreen
import com.application.echo.presentation.search_user.SearchUserScreen
import com.application.echo.presentation.splash.SplashScreen
import com.application.echo.ui.components.scaffold.EchoScaffold
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.delayEach
import kotlinx.coroutines.flow.onEach

@Composable
fun RootNavScreen(
    navigator: Navigator,
    viewModel: RootNavViewModel = hiltViewModel()
) {
    val state = viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow
            .collectLatest { event ->
            when (event) {
                is RootNavEvent.OnAuthStateChanged -> {
                    when (event.authState) {
                        is AuthState.Authenticated -> navigator.navigateToRoot(ConversationScreen)
                        is AuthState.CreateProfile -> navigator.navigateToRoot(CreateProfile)
                        is AuthState.Unauthenticated -> navigator.navigateToRoot(LoginScreen)
                        is AuthState.Initializing -> Unit // wait
                    }
                }
            }
        }
    }

    if (state.value.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    EchoNavHost(
        navigator = navigator,
        startDestination = SplashScreen,
    ) {
        echoComposable<SplashScreen> {
            SplashScreen()
        }
        echoComposable<LoginScreen> {
            LoginScreen(
                onNavigateToRegisterScreen = {
                    navigator.navigateTo(RegisterScreen)
                },
                onNavigateToPhoneAuthScreen = {
                    navigator.navigateTo(OtpScreen)
                },
            )
        }
        echoComposable<PhoneAuthScreen> {
            PhoneAuthScreen(
                onNavigateToOtp = {
                },
                onNavigateToEmailAuth = {
                    navigator.navigateTo(LoginScreen)
                }
            )
        }
        echoComposable<OtpScreen> {
            OtpScreen(
                onNavigateToHome = {
                    navigator.navigateToRoot(ConversationScreen)
                },
                onNavigateBack = {
                    navigator.navigateBack()
                },
            )
        }
        echoComposable<RegisterScreen>(
            transition = EchoTransitionPreset.SlideHorizontal,
        ) {
            RegisterScreen(
                onNavigateToLoginScreen = {
                    navigator.navigateBackTo(LoginScreen)
                },
            )
        }
        echoComposable<CreateProfile>(
            transition = EchoTransitionPreset.SlideHorizontal,
        ) {
            CreateProfileScreen()
        }
        echoComposable<ConversationScreen>(
            transition = EchoTransitionPreset.Fade,
        ) {
            ConversationScreen(
                navigateToAddUser = {
                    navigator.navigateTo(SearchUserScreen)
                },
                navigateToChat = { conversationId, participantName ->
                    navigator.navigateTo(
                        ChatScreen(
                            conversationId = conversationId,
                            participantName = participantName,
                        )
                    )
                },
            )
        }
        echoComposable<SearchUserScreen>(
            transition = EchoTransitionPreset.SlideHorizontal,
        ) {
            SearchUserScreen(
                onNavigateBack = {
                    navigator.navigateBack()
                },
                onNavigateToChat = { conversationId, participantName ->
                    navigator.navigateTo(
                        ChatScreen(
                            conversationId = conversationId,
                            participantName = participantName,
                        )
                    )
                },
            )
        }
        echoComposable<ChatScreen>(
            transition = EchoTransitionPreset.SlideHorizontal,
        ) {
            com.application.echo.presentation.chat.ChatScreen(
                onNavigateBack = {
                    navigator.navigateBack()
                },
            )
        }
    }
}
