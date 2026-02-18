package com.application.echo.rootnav

import androidx.compose.runtime.Composable
import com.application.echo.core.navigation.EchoNavHost
import com.application.echo.core.navigation.Navigator
import com.application.echo.core.navigation.echoComposable
import com.application.echo.core.navigation.echoNavigation
import com.application.echo.core.navigation.transition.EchoTransitionPreset
import com.application.echo.core.navigation.transition.EchoTransitionProviders
import com.application.echo.core.navigation.transition.EchoTransitions
import com.application.echo.feature.auth.screens.login.LoginScreen
import com.application.echo.feature.auth.screens.register.RegisterScreen

@Composable
fun RootNavScreen(
    navigator: Navigator
) {
    EchoNavHost(
        navigator = navigator,
        startDestination = LoginScreen,
        transition = EchoTransitionPreset.SlideHorizontal,
    ) {
        echoComposable<LoginScreen>(
        ){
            LoginScreen(
                onNavigateToRegisterScreen = {
                    navigator.navigateTo(RegisterScreen)
                }
            )
        }
        echoComposable<RegisterScreen>(
            transition = EchoTransitionPreset.SlideHorizontal
        ) {
            RegisterScreen(
                onNavigateToRegisterScreen = {
                    navigator.navigateBackTo(LoginScreen)
                }
            )
        }
    }
}
