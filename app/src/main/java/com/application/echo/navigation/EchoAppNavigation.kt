package com.application.echo.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.application.echo.core.navigation.EchoNavHost
import com.application.echo.core.navigation.Navigator
import com.application.echo.core.navigation.echoComposable
import com.application.echo.feature.auth.navigation.AuthGraph
import com.application.echo.feature.auth.navigation.authGraph
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoOutlinedButton
import com.application.echo.ui.components.button.EchoTextButton
import com.application.echo.ui.design.navigation.Transitions
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Root navigation host when the user is **not** authenticated.
 *
 * Delegates entirely to the auth feature module's [authGraph] extension.
 */
@Composable
fun AuthNavigation(navigator: Navigator) {
    EchoNavHost(
        navigator = navigator,
        startDestination = AuthGraph,
        enterTransition = Transitions.slideInFromEnd,
        exitTransition = Transitions.slideOutToStart,
        popEnterTransition = Transitions.slideInFromStart,
        popExitTransition = Transitions.slideOutToEnd,
    ) {
        authGraph(navigator = navigator)
    }
}

/**
 * Root navigation host when the user **is** authenticated.
 *
 * Houses the main app screens. As feature modules are added (e.g. `feature:home`,
 * `feature:profile`), their `NavGraphBuilder` extensions will replace the placeholder
 * composables below.
 */
@Composable
fun MainNavigation(
    navigator: Navigator,
    onLogoutClick: () -> Unit,
) {
    EchoNavHost(
        navigator = navigator,
        startDestination = HomeRoute,
        enterTransition = Transitions.fadeIn,
        exitTransition = Transitions.fadeOut,
        popEnterTransition = Transitions.slideInFromStart,
        popExitTransition = Transitions.slideOutToEnd,
    ) {
        echoComposable<HomeRoute> {
            HomePlaceholderScreen(
                onProfileClick = { navigator.navigateTo(ProfileRoute) },
                onSettingsClick = { navigator.navigateTo(SettingsRoute) },
                onLogoutClick = onLogoutClick,
            )
        }

        echoComposable<ProfileRoute>(
            enterTransition = Transitions.slideInFromEnd,
            exitTransition = Transitions.slideOutToStart,
        ) {
            ProfilePlaceholderScreen(
                onBackClick = { navigator.navigateBack() },
                onSettingsClick = { navigator.navigateTo(SettingsRoute) },
            )
        }

        echoComposable<SettingsRoute>(
            enterTransition = Transitions.slideInFromEnd,
            exitTransition = Transitions.slideOutToStart,
        ) {
            SettingsPlaceholderScreen(
                onBackClick = { navigator.navigateBack() },
                onLogoutClick = onLogoutClick,
            )
        }
    }
}

// ========================== PLACEHOLDER SCREENS ==========================
// These will move into their own feature modules over time.

@Composable
private fun HomePlaceholderScreen(
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EchoTheme.colorScheme.surface.color)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = EchoTheme.shapes.card,
            color = EchoTheme.colorScheme.surface.high,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Home Feed",
                    style = EchoTheme.typography.headlineMedium,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You're logged in!",
                    style = EchoTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(24.dp))

                EchoFilledButton(
                    onClick = onProfileClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("My Profile")
                }

                Spacer(modifier = Modifier.height(12.dp))

                EchoOutlinedButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Settings")
                }

                Spacer(modifier = Modifier.height(12.dp))

                EchoTextButton(onClick = onLogoutClick) {
                    Text("Log Out")
                }
            }
        }
    }
}

@Composable
private fun ProfilePlaceholderScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EchoTheme.colorScheme.surface.color)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = EchoTheme.shapes.card,
            color = EchoTheme.colorScheme.surface.high,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Profile",
                    style = EchoTheme.typography.headlineMedium,
                )

                Spacer(modifier = Modifier.height(24.dp))

                EchoOutlinedButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Settings")
                }

                Spacer(modifier = Modifier.height(12.dp))

                EchoTextButton(onClick = onBackClick) {
                    Text("Back")
                }
            }
        }
    }
}

@Composable
private fun SettingsPlaceholderScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EchoTheme.colorScheme.surface.color)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = EchoTheme.shapes.card,
            color = EchoTheme.colorScheme.surface.high,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Settings",
                    style = EchoTheme.typography.headlineMedium,
                )

                Spacer(modifier = Modifier.height(24.dp))

                EchoFilledButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Log Out")
                }

                Spacer(modifier = Modifier.height(12.dp))

                EchoTextButton(onClick = onBackClick) {
                    Text("Back")
                }
            }
        }
    }
}
