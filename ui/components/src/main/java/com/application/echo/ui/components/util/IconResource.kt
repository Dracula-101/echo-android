package com.application.echo.ui.components.util

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

/**
 * Abstraction over different icon sources.
 *
 * Allows components to accept any icon type without caring whether
 * it comes from a vector drawable, resource ID, or custom painter.
 *
 * ```kotlin
 * val icon: IconResource = IconResource.Vector(Icons.Default.Close)
 * val icon: IconResource = IconResource.Drawable(R.drawable.ic_star)
 * ```
 */
sealed class IconResource {
    data class Vector(val imageVector: ImageVector) : IconResource()
    data class Drawable(@DrawableRes val resId: Int) : IconResource()
    data class IconPainter(val painter: Painter) : IconResource()
}

/**
 * Renders this [IconResource] as a composable icon or image.
 *
 * @param modifier Modifier applied to the rendered icon.
 * @param color Tint color; defaults to [LocalContentColor] for vector/drawable,
 *   or applied as a [ColorFilter] for painter-based icons.
 * @param contentDescription Accessibility description for the icon.
 */
@Composable
fun IconResource.Paint(
    modifier: Modifier = Modifier,
    color: Color? = null,
    contentDescription: String? = null,
) {
    when (this) {
        is IconResource.Vector -> {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = color ?: LocalContentColor.current,
                modifier = modifier,
            )
        }

        is IconResource.Drawable -> {
            Icon(
                painter = painterResource(resId),
                contentDescription = contentDescription,
                tint = color ?: LocalContentColor.current,
                modifier = modifier,
            )
        }

        is IconResource.IconPainter -> {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                colorFilter = color?.let { ColorFilter.tint(it) },
                modifier = modifier,
            )
        }
    }
}
