package com.application.echo.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.ToComposable
import com.application.echo.ui.design.theme.EchoTheme

@Composable
fun EchoOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    icon: IconResource? = null,
    content: @Composable () -> Unit = {},
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = EchoTheme.colorScheme.outlinedButtonColors(variant),
        shape = EchoTheme.shapes.button,
        border = BorderStroke(
            width = EchoTheme.dimen.border.medium,
            color = EchoTheme.colorScheme.outlinedButtonBorderColor(variant, enabled = enabled)
        ),
        contentPadding = PaddingValues(
            horizontal = EchoTheme.spacing.padding.medium,
            vertical = EchoTheme.spacing.padding.small,
        ),
        content = {
            Box(
                modifier = Modifier.then(
                    if (icon != null) Modifier
                        .padding(end = EchoTheme.spacing.padding.extraSmall)
                        .size(EchoTheme.typography.bodyLarge.fontSize.value.dp * 1.25f)
                    else Modifier
                )
            ) {
                icon?.ToComposable()
            }
            ProvideTextStyle(
                value = EchoTheme.typography.bodyLarge,
                content = content
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun PreviewEchoOutlinedButton() {
    EchoTheme {
        FlowRow(
            modifier = Modifier
                .background(EchoTheme.colorScheme.background.color)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ButtonVariant.entries.forEach {
                EchoOutlinedButton(
                    variant = it,
                ) {
                    Text(it.name)
                }
            }
            ButtonVariant.entries.forEach {
                EchoOutlinedButton(
                    variant = it,
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Favorite,
                        contentDescription = it.name,
                    )
                    Spacer(modifier = Modifier.width(EchoTheme.spacing.gap.small))
                    Text(it.name)
                }
            }
       }
    }
}