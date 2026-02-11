package com.application.echo.ui.components.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme

@Composable
fun EchoLoadingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isLoading: Boolean = true,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    content: @Composable () -> Unit = {},
) {
    val buttonTheme = EchoTheme.colorScheme.outlinedButtonColors(variant)
    EchoOutlinedButton(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier,
        variant = variant,
        content = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(EchoTheme.dimen.icon.small),
                    strokeWidth = 2.dp,
                    color = buttonTheme.disabledContentColor,
                )
                Spacer(Modifier.size(EchoTheme.spacing.gap.small))
            }
            content()
        },
    )
}

@PreviewLightDark
@Composable
private fun PreviewEchoLoadingButton() {
    EchoTheme {
        Surface(
            modifier = Modifier.padding(10.dp)
        ) {
            EchoLoadingButton(isLoading = true) {
                Text("Click me")
            }
        }
    }
}