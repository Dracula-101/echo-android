package com.application.echo.ui.components.icon

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class EchoIconButtonSize(
    val buttonSize: Dp,
    val iconSize: Dp,
) {
    Small(32.dp, 16.dp),
    Medium(40.dp, 20.dp),
    Large(48.dp, 24.dp),
    XL(56.dp, 28.dp),
}