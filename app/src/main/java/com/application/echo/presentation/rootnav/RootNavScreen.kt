package com.application.echo.presentation.rootnav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
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
import com.application.echo.presentation.chat.ChatScreen
import com.application.echo.presentation.create_profile.CreateProfileScreen
import com.application.echo.presentation.conversation.ConversationScreen
import com.application.echo.presentation.login.LoginScreen
import com.application.echo.presentation.otp.OtpScreen
import com.application.echo.presentation.phone.PhoneAuthScreen
import com.application.echo.presentation.register.RegisterScreen
import com.application.echo.presentation.search_user.SearchUserScreen
import com.application.echo.presentation.splash.SplashScreen
import kotlinx.coroutines.flow.collectLatest

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
                        is AuthState.Authenticated -> navigator.navigateToRoot(ConversationScreenRoute)
                        is AuthState.CreateProfile -> navigator.navigateToRoot(CreateProfileRoute)
                        is AuthState.Unauthenticated -> navigator.navigateToRoot(LoginScreenRoute)
                        is AuthState.OtpVerification -> navigator.navigateTo(OtpScreenRoute)
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
        startDestination = SplashScreenRoute,
    ) {
        echoComposable<SplashScreenRoute> {
            SplashScreen()
        }
        echoComposable<LoginScreenRoute> {
            LoginScreen(
                onNavigateToRegisterScreen = {
                    navigator.navigateTo(PhoneAuthScreenRoute)
                },
                onNavigateToPhoneAuthScreen = {
                    navigator.navigateTo(PhoneAuthScreenRoute)
                },
            )
        }
        echoComposable<PhoneAuthScreenRoute> {
            PhoneAuthScreen(
                onNavigateToEmailAuth = {
                    navigator.navigateBackTo(LoginScreenRoute)
                }
            )
        }
        echoComposable<OtpScreenRoute> {
            OtpScreen(
                onNavigateBack = {
                    navigator.navigateBack()
                },
            )
        }
        echoComposable<RegisterScreenRoute>(
            transition = EchoTransitionPreset.SlideHorizontal,
        ) {
            RegisterScreen(
                onNavigateToLoginScreen = {
                    navigator.navigateBackTo(LoginScreenRoute)
                },
            )
        }
        echoComposable<CreateProfileRoute>(
            transition = EchoTransitionPreset.SlideHorizontal,
        ) {
            CreateProfileScreen()
        }
        echoComposable<ConversationScreenRoute>(
            transition = EchoTransitionPreset.Fade,
        ) {
            ConversationScreen(
                navigateToAddUser = {
                    navigator.navigateTo(SearchUserScreenRoute)
                },
                navigateToChat = { conversationId, participantName ->
                    navigator.navigateTo(
                        ChatScreenRoute(
                            conversationId = conversationId,
                            participantName = participantName,
                        )
                    )
                },
            )
        }
        echoComposable<SearchUserScreenRoute>(
            transition = EchoTransitionPreset.SlideHorizontal,
        ) {
            SearchUserScreen(
                onNavigateBack = {
                    navigator.navigateBack()
                },
                onNavigateToChat = { conversationId, participantName ->
                    navigator.navigateTo(
                        ChatScreenRoute(
                            conversationId = conversationId,
                            participantName = participantName,
                        )
                    )
                },
            )
        }
        echoComposable<ChatScreenRoute>(
            transition = EchoTransitionPreset.SlideHorizontal,
        ) {
            ChatScreen(
                onNavigateBack = {
                    navigator.navigateBack()
                },
            )
        }
    }
}
