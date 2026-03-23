package com.application.echo.rootnav

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
import com.application.echo.feature.auth.model.AuthState
import com.application.echo.feature.auth.screens.login.LoginScreen
import com.application.echo.feature.auth.screens.register.RegisterScreen
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
        startDestination = if (state.value.userLoggedIn) HomeScreen else LoginScreen,
        transition = EchoTransitionPreset.SlideHorizontal,
    ) {
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
                onRegisterSuccess = {
                },
            )
        }
        echoComposable<HomeScreen>(
            transition = EchoTransitionPreset.SlideHorizontal,
        ) {
            EchoScaffold {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Welcome to the Home Screen!")
                }
            }
        }
    }
}
