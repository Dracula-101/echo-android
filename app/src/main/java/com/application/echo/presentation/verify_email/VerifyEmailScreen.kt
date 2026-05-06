package com.application.echo.presentation.verify_email

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.echo.ui.components.scaffold.EchoScaffold

@Composable
fun VerifyEmail(
    viewModel: VerifyEmailViewModel = hiltViewModel()
) {
    val state = viewModel.stateFlow.collectAsStateWithLifecycle()
    EchoScaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Verify Email Screen")
            Text("Token: ${state.value.token}")
        }
    }
}