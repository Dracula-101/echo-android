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
import com.application.echo.presentation.home.HomeScreen
import com.application.echo.presentation.login.LoginScreen
import com.application.echo.presentation.register.RegisterScreen
import com.application.echo.presentation.splash.SplashScreen
import com.application.echo.ui.components.scaffold.EchoScaffold
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RootNavScreen(
    navigator: Navigator,
    viewModel: RootNavViewModel = hiltViewModel()
) {
    val state = viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RootNavEvent.OnAuthStateChanged -> {
                    when (event.authState) {
                        is AuthState.Authenticated -> navigator.navigateToRoot(HomeScreen)
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
        echoComposable<HomeScreen>(
            transition = EchoTransitionPreset.Fade,
        ) {
            HomeScreen()
        }
    }
}
