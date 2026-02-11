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

sealed class IconResource {
    data class Vector(val imageVector: ImageVector) : IconResource()
    data class Drawable(@DrawableRes val resId: Int) : IconResource()
    data class IconPainter(val painter: Painter) : IconResource()
}

@Composable
fun IconResource.ToComposable(
    modifier: Modifier = Modifier,
    color: Color? = null,
) {
    when (this) {
        is IconResource.Vector -> {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = color ?: LocalContentColor.current,
                modifier = modifier
            )
        }

        is IconResource.Drawable -> {
            Icon(
                painter = painterResource(resId),
                contentDescription = null,
                tint = color ?: LocalContentColor.current,
                modifier = modifier
            )
        }

        is IconResource.IconPainter -> {
            Image(
                painter = painter,
                contentDescription = null,
                colorFilter = color?.let { ColorFilter.tint(it) },
                modifier = modifier,
            )
        }
    }
}