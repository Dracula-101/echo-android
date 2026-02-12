package com.application.echo.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoOutlinedButton
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.spacing.EchoSpacer
import com.application.echo.ui.components.spacing.EchoSpacerSize
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Action dialog with end-aligned buttons (primary + optional secondary).
 *
 * The primary action is always shown; the secondary action only appears
 * when both [secondaryActionText] and [onSecondaryAction] are provided.
 *
 * ```kotlin
 * EchoActionDialog(
 *     onDismissRequest = ::dismiss,
 *     title = "Delete item",
 *     content = "This action cannot be undone.",
 *     primaryActionText = "Delete",
 *     onPrimaryAction = ::deleteItem,
 *     secondaryActionText = "Cancel",
 *     onSecondaryAction = ::dismiss,
 *     destructive = true,
 * )
 * ```
 */
@Composable
fun EchoActionDialog(
    onDismissRequest: () -> Unit,
    title: String,
    content: String,
    primaryActionText: String,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    destructive: Boolean = false,
) {
    val dialogColors = EchoTheme.colorScheme.dialogColors()

    EchoDialogShell(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        Text(
            text = title,
            style = EchoTheme.typography.headlineMedium,
            color = dialogColors.title,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        EchoSpacer(size = EchoSpacerSize.Small)
        Text(
            text = content,
            style = EchoTheme.typography.bodyLarge,
            color = dialogColors.content.copy(alpha = 0.8f),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        EchoSpacer(size = EchoSpacerSize.Large)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (secondaryActionText != null && onSecondaryAction != null) {
                EchoOutlinedButton(
                    onClick = { onSecondaryAction(); onDismissRequest() },
                    modifier = Modifier.wrapContentHeight(),
                ) {
                    Text(
                        text = secondaryActionText,
                        style = EchoTheme.typography.bodyLarge,
                    )
                }
                Spacer(modifier = Modifier.width(EchoTheme.spacing.gap.medium))
            }
            EchoFilledButton(
                onClick = { onPrimaryAction(); onDismissRequest() },
                variant = if (destructive) EchoVariant.Error else EchoVariant.Primary,
                modifier = Modifier.wrapContentHeight(),
            ) {
                Text(
                    text = primaryActionText,
                    style = EchoTheme.typography.bodyLarge,
                )
            }
        }
    }
}

/**
 * Action dialog with fully custom composable slots for title, content, and actions.
 *
 * Use the [String]-based overload when you only need simple labels.
 */
@Composable
fun EchoActionDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    actionsAlignment: Alignment.Horizontal = Alignment.End,
) {
    val dialogColors = EchoTheme.colorScheme.dialogColors()

    EchoDialogShell(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        ProvideTextStyle(
            value = EchoTheme.typography.headlineMedium.copy(color = dialogColors.title),
            content = title,
        )
        EchoSpacer(size = EchoSpacerSize.Small)
        ProvideTextStyle(
            value = EchoTheme.typography.bodyLarge.copy(
                color = dialogColors.content.copy(alpha = 0.8f),
            ),
            content = content,
        )
        EchoSpacer(size = EchoSpacerSize.Large)
        ProvideTextStyle(
            value = EchoTheme.typography.bodyLarge.copy(color = dialogColors.content),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    EchoTheme.spacing.gap.medium,
                    actionsAlignment,
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                actions()
            }
        }
    }
}
