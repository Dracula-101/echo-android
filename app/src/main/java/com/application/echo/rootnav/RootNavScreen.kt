package com.application.echo.rootnav

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.core.navigation.EchoNavHost
import com.application.echo.core.navigation.Navigator
import com.application.echo.core.navigation.echoComposable
import com.application.echo.core.navigation.transition.EchoTransitionPreset
import com.application.echo.feature.auth.model.UserState
import com.application.echo.feature.auth.screens.login.LoginEvent
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
                is RootNavEvent.AuthStateChanged -> {
                    if (event.userState == UserState.Empty) {
                        navigator.navigateToRoot(LoginScreen)
                    } else {
                        navigator.navigateToRoot(HomeScreen)
                    }
                }
            }
        }
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
            transition = EchoTransitionPreset.None,
        ) {
            EchoScaffold {
                Box {
                    Text(text = "Welcome to the Home Screen!")
                }
            }
        }
    }
}
