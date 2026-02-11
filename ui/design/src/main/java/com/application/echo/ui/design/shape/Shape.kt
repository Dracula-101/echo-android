package com.application.echo.ui.design.shape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

internal val echoShapes: EchoShapes = EchoShapes(
    card = RoundedCornerShape(12.dp),
    button = RoundedCornerShape(10.dp),
    input = RoundedCornerShape(8.dp),
    chip = RoundedCornerShape(24.dp),
    dialog = RoundedCornerShape(16.dp),
    dropdown = RoundedCornerShape(8.dp),
    snackbar = RoundedCornerShape(8.dp),
    fab = RoundedCornerShape(16.dp),
    progressIndicator = RoundedCornerShape(4.dp),
    bottomSheet = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    ),
    icon = RoundedCornerShape(6.dp),
)