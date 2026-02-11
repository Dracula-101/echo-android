package com.application.echo.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import com.application.echo.core.navigation.Navigator
import com.application.echo.core.navigation.echoComposable
import com.application.echo.core.navigation.echoNavigation
import com.application.echo.feature.auth.login.LoginScreen
import com.application.echo.feature.auth.signup.SignUpScreen

/**
 * Registers the authentication navigation graph into the parent [NavGraphBuilder].
 *
 * This is the **public API** of the auth feature module. The app module calls this
 * extension to wire the auth flow into the root navigation without needing to know
 * about individual screen composables or routes (except [AuthGraph]).
 *
 * @param navigator The app-wide [Navigator] for handling navigation commands.
 */
fun NavGraphBuilder.authGraph(
    navigator: Navigator,
) {
    echoNavigation<AuthGraph>(startDestination = LoginRoute::class) {
        echoComposable<LoginRoute> {
            LoginScreen(
                onSignUpClick = { navigator.navigateTo(SignUpRoute) },
            )
        }

        echoComposable<SignUpRoute> {
            SignUpScreen(
                onBackToLoginClick = { navigator.navigateBack() },
            )
        }
    }
}
