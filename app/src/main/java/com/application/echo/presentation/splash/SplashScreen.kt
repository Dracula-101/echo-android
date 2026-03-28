package com.application.echo.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.application.echo.ui.components.scaffold.EchoScaffold


@Composable
fun SplashScreen() {
    EchoScaffold {
        Box(
            modifier = Modifier.fillMaxSize()
        )
    }
}