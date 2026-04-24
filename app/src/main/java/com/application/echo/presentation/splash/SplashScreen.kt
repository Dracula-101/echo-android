package com.application.echo.presentation.splash

import android.provider.Settings.Global.getString
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.application.echo.R
import com.application.echo.ui.components.scaffold.EchoScaffold
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha20
import com.application.echo.ui.design.utils.alpha30
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha60
import com.application.echo.ui.design.utils.alpha70
import com.application.echo.ui.design.utils.alpha90


@Composable
fun SplashScreen() {
    EchoScaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = EchoTheme.spacing.padding.extraLarge)
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                EchoTheme.colorScheme.surface.highest.alpha70,
                                EchoTheme.colorScheme.surface.high.alpha30,
                                EchoTheme.colorScheme.background.color
                            ),
                            center = Offset(540f, 900f), // adjust for screen center
                            radius = 900f
                        )
                    )
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.medium, Alignment.CenterVertically)
            ) {
                Text(
                    text = stringResource(R.string.app_name).lowercase(),
                    style = EchoTheme.typography.displayMedium,
                    color = EchoTheme.colorScheme.surface.onColor,
                )
                Text(
                    text = "warm, private, alive",
                    style = EchoTheme.typography.bodyMedium,
                    color = EchoTheme.colorScheme.surface.onColor.alpha50,
                )
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = EchoTheme.spacing.gap.medium),
                horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.extraSmall, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = EchoTheme.colorScheme.surface.onColor.alpha50,
                    modifier = Modifier
                        .size(EchoTheme.dimen.icon.extraSmall)
                )
                Text(
                    text = "not end-to-end encrypted",
                    style = EchoTheme.typography.bodySmall,
                    color = EchoTheme.colorScheme.surface.onColor.alpha50,
                    modifier = Modifier
                )
            }
        }
    }
}