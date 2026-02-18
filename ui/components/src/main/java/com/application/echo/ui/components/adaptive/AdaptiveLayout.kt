package com.application.echo.ui.components.adaptive

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.application.echo.ui.util.rememberWindowSize
import com.application.echoplatform.model.WindowSize

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    firstContent: @Composable () -> Unit,
    secondContent: @Composable () -> Unit,
    layoutContentRatio: Float = 0.5f,
) {
    val windowSizeClass = calculateWindowSizeClass(LocalActivity.current!!)
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Column(modifier) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    firstContent()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    secondContent()
                }
            }
        }
        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded-> {
            Row(modifier) {
                Box(
                    modifier = Modifier
                        .weight(layoutContentRatio)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    firstContent()
                }
                Box(
                    modifier = Modifier
                        .weight(1 - layoutContentRatio)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    secondContent()
                }
            }
        }
    }
}