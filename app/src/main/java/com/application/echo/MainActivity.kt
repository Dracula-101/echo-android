package com.application.echo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.application.echo.core.analytics.Analytics
import com.application.echo.core.analytics.AnalyticsEvent
import com.application.echo.core.analytics.LocalAnalytics
import com.application.echo.core.navigation.LocalNavigator
import com.application.echo.core.navigation.Navigator
import com.application.echo.presentation.rootnav.RootNavScreen
import com.application.echo.ui.design.theme.EchoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analytics: Analytics

    @Inject
    lateinit var navigator: Navigator

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* no-op — best effort */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        analytics.trackEvent(AnalyticsEvent.AppEvent.opened("launcher"))

        requestNotificationPermission()
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalAnalytics provides analytics,
                LocalNavigator provides navigator,
            ) {
                EchoTheme(isDarkTheme = true) {
                    RootNavScreen(navigator)
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) return
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
