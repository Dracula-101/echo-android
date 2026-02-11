package com.application.echo.ui.components.button

import android.R
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.ToComposable
import com.application.echo.ui.design.theme.EchoTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EchoIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    icon: IconResource,
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
        colors = EchoTheme.colorScheme.iconButtonColors(),
        content = {
            icon.ToComposable(
                modifier = Modifier.size(size),
            )
        }
    )
}


@PreviewLightDark
@Composable
fun PreviewEchoIconButton() {
    EchoTheme {
        EchoIconButton(
            onClick = { /*TODO*/ },
            icon = IconResource.Drawable(R.drawable.ic_input_add)
        )
    }
}