package com.application.echo.ui.components.spacing

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EchoSpacer(
    modifier: Modifier = Modifier,
    size: EchoSpacerSize = EchoSpacerSize.Medium,
) {
    Spacer(modifier = modifier.size(size.size.dp))
}

enum class EchoSpacerSize(val size: Int) {
    ExtraSmall(4),
    Small(8),
    Medium(16),
    Large(24),
    ExtraLarge(32),
    Huge(48);
}