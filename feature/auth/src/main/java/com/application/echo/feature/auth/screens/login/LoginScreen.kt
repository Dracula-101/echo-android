package com.application.echo.feature.auth.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.application.echo.ui.components.scaffold.EchoScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
) {
    EchoScaffold (
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ){
        Text("hello word")
    }
}