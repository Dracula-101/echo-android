package com.application.echo.ui.components.fab

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Discrete FAB sizes — Small, Medium (default), Large, ExtraLarge.
 *
 * Mockup mapping: 44 / 56 / 64 / 80 dp.
 */
enum class FabSize(
    val container: Dp,
    val icon: Dp,
) {
    Small(container = 44.dp, icon = 18.dp),
    Medium(container = 56.dp, icon = 22.dp),
    Large(container = 64.dp, icon = 26.dp),
    ExtraLarge(container = 80.dp, icon = 30.dp),
}
