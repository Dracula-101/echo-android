package com.application.echo.ui.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.application.echo.ui.components.R
import com.application.echo.ui.components.button.ButtonVariant
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoOutlinedButton
import com.application.echo.ui.components.spacing.EchoSpacer
import com.application.echo.ui.components.spacing.EchoSpacerSize
import com.application.echo.ui.design.theme.EchoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoCompactActionDialog(
    onDismissRequest: () -> Unit,
    title: String,
    content: String,
    primaryActionText: String,
    onPrimaryAction: () -> Unit,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    destructive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val dialogColors = EchoTheme.colorScheme.dialogColors()
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = EchoTheme.shapes.dialog,
                colors = EchoTheme.colorScheme.dialogCardColors(),
                border = BorderStroke(2.dp, dialogColors.border.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box {
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.align(Alignment.TopEnd),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_close_24),
                            contentDescription = "Close dialog",
                            modifier = Modifier
                                .width(18.dp)
                                .height(18.dp),
                            colorFilter = ColorFilter.tint(EchoTheme.colorScheme.primary.color),
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = title,
                            style = EchoTheme.typography.headlineMedium,
                            color = dialogColors.title,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        EchoSpacer(size = EchoSpacerSize.Small)
                        Text(
                            text = content,
                            style = EchoTheme.typography.bodyLarge,
                            color = dialogColors.content.copy(alpha = 0.8f),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        EchoSpacer(size = EchoSpacerSize.Large)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (secondaryActionText != null && onSecondaryAction != null) {
                                EchoOutlinedButton(
                                    onClick = {
                                        onSecondaryAction()
                                        onDismissRequest()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .wrapContentHeight(),
                                ) {
                                    Text(
                                        text = secondaryActionText,
                                        style = EchoTheme.typography.bodyLarge,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            EchoFilledButton(
                                onClick = {
                                    onPrimaryAction()
                                    onDismissRequest()
                                },
                                variant = if (destructive) ButtonVariant.Error else ButtonVariant.Primary,
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentHeight(),
                            ) {
                                Text(
                                    text = primaryActionText,
                                    style = EchoTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoCompactActionDialog(
    onDismissRequest: () -> Unit,
    title: () -> Composable,
    content: () -> Composable,
    actions: @Composable RowScope.() -> Unit,
    actionsAlignment: Alignment.Horizontal = Alignment.End,
    modifier: Modifier = Modifier
) {
    val dialogColors = EchoTheme.colorScheme.dialogColors()
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = EchoTheme.shapes.dialog,
                colors = EchoTheme.colorScheme.dialogCardColors(),
                border = BorderStroke(2.dp, dialogColors.border.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box {
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.align(Alignment.TopEnd),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_close_24),
                            contentDescription = "Close dialog",
                            modifier = Modifier
                                .width(18.dp)
                                .height(18.dp),
                            colorFilter = ColorFilter.tint(EchoTheme.colorScheme.primary.color),
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        ProvideTextStyle(EchoTheme.typography.headlineMedium.copy(color = dialogColors.title)) {
                            title()
                        }
                        EchoSpacer(size = EchoSpacerSize.Small)
                        ProvideTextStyle(
                            EchoTheme.typography.bodyLarge.copy(
                                color = dialogColors.content.copy(
                                    alpha = 0.8f
                                )
                            )
                        ) {
                            content()
                        }
                        EchoSpacer(size = EchoSpacerSize.Large)
                        ProvideTextStyle(EchoTheme.typography.bodyLarge) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    8.dp,
                                    actionsAlignment
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                actions()
                            }
                        }
                    }
                }
            }
        }
    }
}
