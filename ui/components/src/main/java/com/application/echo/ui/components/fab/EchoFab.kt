package com.application.echo.ui.components.fab

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.common.containerColor
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.components.common.onContainerColor
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Floating action button — the primary "next step" affordance on a screen.
 *
 * ```kotlin
 * EchoFab(icon = IconResource.Vector(Icons.Default.Edit), onClick = ::compose)
 * EchoFab(icon = IconResource.Vector(Icons.Default.Phone), variant = EchoVariant.Secondary, size = FabSize.Large)
 * ```
 *
 * @param icon Icon rendered inside the FAB.
 * @param onClick Tap callback.
 * @param size One of [FabSize] tokens — Small (44), Medium (56), Large (64), ExtraLarge (80).
 * @param style Visual treatment — see [FabStyle].
 * @param variant Color family. Defaults to [EchoVariant.Primary].
 * @param shape Override for the FAB shape. Defaults to [EchoTheme.shapes.fab]; pass [CircleShape] for the round XL variant.
 */
@Composable
fun EchoFab(
    icon: IconResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: FabSize = FabSize.Medium,
    style: FabStyle = FabStyle.Filled,
    variant: EchoVariant = EchoVariant.Primary,
    shape: CornerBasedShape = EchoTheme.shapes.fab,
    contentDescription: String? = null,
) {
    val (container, content) = fabColors(style, variant)

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(size.container)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 24.dp,
                    spread = 0.dp,
                    color = container.copy(alpha = 0.35f),
                    offset = DpOffset(x = 0.dp, y = 8.dp),
                ),
            ),
        shape = shape,
        containerColor = container,
        contentColor = content,
        elevation = FloatingActionButtonDefaults.loweredElevation(),
    ) {
        icon.Paint(
            modifier = Modifier.size(size.icon),
            color = content,
            contentDescription = contentDescription,
        )
    }
}

/**
 * Extended FAB — pill-shaped action with both an icon and a label.
 *
 * Reserved for screens with a single dominant action ("New chat", "Compose").
 *
 * ```kotlin
 * EchoExtendedFab(
 *     icon = IconResource.Vector(Icons.Default.Edit),
 *     text = "New chat",
 *     onClick = ::compose,
 * )
 * ```
 */
@Composable
fun EchoExtendedFab(
    icon: IconResource,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: FabStyle = FabStyle.Filled,
    variant: EchoVariant = EchoVariant.Primary,
    contentDescription: String? = null,
) {
    val (container, content) = fabColors(style, variant)
    val shape = EchoTheme.shapes.fab

    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier.dropShadow(
            shape = shape,
            shadow = Shadow(
                radius = 24.dp,
                spread = 0.dp,
                color = container.copy(alpha = 0.35f),
                offset = DpOffset(x = 0.dp, y = 8.dp),
            ),
        ),
        shape = shape,
        containerColor = container,
        contentColor = content,
        elevation = FloatingActionButtonDefaults.loweredElevation(),
        icon = {
            icon.Paint(
                modifier = Modifier.size(FabSize.Medium.icon),
                color = content,
                contentDescription = contentDescription,
            )
        },
        text = {
            CompositionLocalProvider(LocalContentColor provides content) {
                ProvideTextStyle(EchoTheme.typography.labelLarge) {
                    Text(text = text, color = content)
                }
            }
        },
    )
}

@Composable
private fun fabColors(
    style: FabStyle,
    variant: EchoVariant,
): Pair<Color, Color> = when (style) {
    FabStyle.Filled -> variant.color() to variant.onColor()
    FabStyle.Tonal -> variant.containerColor() to variant.onContainerColor()
    FabStyle.Dark -> EchoTheme.colorScheme.inverse.surface to EchoTheme.colorScheme.inverse.onSurface
}
