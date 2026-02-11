package com.application.echo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.application.echo.core.analytics.Analytics
import com.application.echo.core.analytics.AnalyticsEvent
import com.application.echo.core.analytics.LocalAnalytics
import com.application.echo.core.navigation.Navigator
import com.application.echo.navigation.AuthNavigation
import com.application.echo.navigation.MainNavigation
import com.application.echo.ui.design.theme.EchoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analytics: Analytics

    @Inject
    lateinit var navigator: Navigator


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        analytics.trackEvent(AnalyticsEvent.AppEvent.opened("launcher"))

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalAnalytics provides analytics) {
                EchoTheme {
                    EchoApp(
                        navigator = navigator,
                    )
                }
            }
        }
    }
}

@Composable
private fun EchoApp(
    navigator: Navigator,
) {
    val scope = rememberCoroutineScope()

    Crossfade(
        targetState = null,
        label = "auth_crossfade",
    ) { state ->
        when (state) {
            else -> AuthNavigation(
                navigator = navigator,
            )
        }
    }
}
