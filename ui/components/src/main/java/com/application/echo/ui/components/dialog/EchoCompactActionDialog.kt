package com.application.echo.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
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
 * Compact dialog with equal-weight action buttons in a horizontal row.
 *
 * Buttons are more compressed than [EchoActionDialog] and each button
 * takes equal weight so they fill the width evenly.
 *
 * ```kotlin
 * EchoCompactActionDialog(
 *     onDismissRequest = ::dismiss,
 *     title = "Delete Account?",
 *     content = "This action cannot be undone.",
 *     primaryActionText = "Delete",
 *     onPrimaryAction = ::deleteAccount,
 *     secondaryActionText = "Cancel",
 *     onSecondaryAction = ::dismiss,
 *     destructive = true,
 * )
 * ```
 */
@Composable
fun EchoCompactActionDialog(
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
        compact = true,
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
            horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (secondaryActionText != null && onSecondaryAction != null) {
                EchoOutlinedButton(
                    onClick = { onSecondaryAction(); onDismissRequest() },
                    modifier = Modifier.weight(1f).wrapContentHeight(),
                ) {
                    Text(text = secondaryActionText, maxLines = 1, textAlign = TextAlign.Center)
                }
            }
            EchoFilledButton(
                onClick = { onPrimaryAction(); onDismissRequest() },
                variant = if (destructive) EchoVariant.Error else EchoVariant.Primary,
                modifier = Modifier.weight(1f).wrapContentHeight(),
            ) {
                Text(text = primaryActionText, maxLines = 1, textAlign = TextAlign.Center)
            }
        }
    }
}

/**
 * Compact dialog with fully custom composable slots.
 */
@Composable
fun EchoCompactActionDialog(
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
        compact = true,
    ) {
        ProvideTextStyle(EchoTheme.typography.headlineMedium.copy(color = dialogColors.title)) {
            title()
        }
        EchoSpacer(size = EchoSpacerSize.Small)
        ProvideTextStyle(EchoTheme.typography.bodyLarge.copy(color = dialogColors.content.copy(alpha = 0.8f))) {
            content()
        }
        EchoSpacer(size = EchoSpacerSize.Large)
        ProvideTextStyle(EchoTheme.typography.bodyLarge) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.small, actionsAlignment),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                actions()
            }
        }
    }
}
