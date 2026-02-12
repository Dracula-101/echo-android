package com.application.echo.ui.components.button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Icon-only button — ideal for toolbar actions, close buttons, etc.
 *
 * ```kotlin
 * EchoIconButton(
 *     icon = IconResource.Vector(Icons.Default.Close),
 *     onClick = ::dismiss,
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EchoIconButton(
    icon: IconResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    size: Dp = EchoTheme.dimen.icon.medium,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shapes = IconButtonDefaults.shapes(
            shape = EchoTheme.shapes.button,
            pressedShape = EchoTheme.shapes.button,
        ),
        colors = EchoTheme.colorScheme.iconButtonColors(variant),
        content = {
            icon.Paint(
                modifier = Modifier.size(size),
            )
        },
    )
}