package com.application.echo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.application.echo.core.analytics.Analytics
import com.application.echo.core.analytics.AnalyticsEvent
import com.application.echo.core.analytics.LocalAnalytics
import com.application.echo.core.navigation.LocalNavigator
import com.application.echo.core.navigation.Navigator
import com.application.echo.features.auth.model.AuthState
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.presentation.rootnav.RootNavScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analytics: Analytics

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            authRepository.authStateFlow.value is AuthState.Initializing
        }
        super.onCreate(savedInstanceState)
        analytics.trackEvent(AnalyticsEvent.AppEvent.opened("launcher"))
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalAnalytics provides analytics,
                LocalNavigator provides navigator,
            ) {
                EchoTheme(isDarkTheme = true) {
                    RootNavScreen(
                        navigator = navigator,
                    )
                }
            }
        }
    }
}
