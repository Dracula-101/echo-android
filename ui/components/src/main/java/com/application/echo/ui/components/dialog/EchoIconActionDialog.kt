package com.application.echo.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoOutlinedButton
import com.application.echo.ui.components.spacing.EchoSpacer
import com.application.echo.ui.components.spacing.EchoSpacerSize
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

private val ICON_SIZE = 80.dp

/**
 * Center-aligned dialog with a prominent icon, title, message, and action buttons.
 *
 * Ideal for confirmation dialogs where a visual icon reinforces the context.
 *
 * ```kotlin
 * EchoIconActionDialog(
 *     onDismissRequest = ::dismiss,
 *     title = "Success!",
 *     message = "Your file has been uploaded.",
 *     icon = IconResource.Drawable(R.drawable.ic_check_circle),
 *     positiveButtonText = "OK",
 *     onPositiveButtonClick = ::dismiss,
 * )
 * ```
 */
@Composable
fun EchoIconActionDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: IconResource? = null,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    onPositiveButtonClick: (() -> Unit)? = null,
    onNegativeButtonClick: (() -> Unit)? = null,
) {
    val dialogColors = EchoTheme.colorScheme.dialogColors()

    EchoDialogShell(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        compact = true,
        showCloseButton = false,
    ) {
        EchoSpacer(size = EchoSpacerSize.Small)

        icon?.Paint(
            modifier = Modifier
                .size(ICON_SIZE)
                .align(Alignment.CenterHorizontally),
            color = EchoTheme.colorScheme.primary.color,
        )

        Text(
            text = title,
            style = EchoTheme.typography.headlineMedium,
            color = dialogColors.title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = EchoTheme.spacing.padding.small),
        )
        EchoSpacer(size = EchoSpacerSize.Small)
        Text(
            text = message,
            style = EchoTheme.typography.titleMedium,
            color = dialogColors.content.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = EchoTheme.spacing.padding.small),
        )
        EchoSpacer()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = EchoTheme.spacing.padding.small),
            horizontalArrangement = Arrangement.spacedBy(
                EchoTheme.spacing.gap.small,
                Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (negativeButtonText != null && onNegativeButtonClick != null) {
                EchoOutlinedButton(
                    onClick = { onNegativeButtonClick(); onDismissRequest() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = negativeButtonText,
                        style = EchoTheme.typography.bodyLarge,
                    )
                }
            }
            if (positiveButtonText != null && onPositiveButtonClick != null) {
                EchoFilledButton(
                    onClick = { onPositiveButtonClick(); onDismissRequest() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = positiveButtonText,
                        style = EchoTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}
