package com.application.echo.ui.components.dialog

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
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
fun EchoActionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: String,
    content: String,
    primaryActionText: String,
    onPrimaryAction: () -> Unit,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    destructive: Boolean = false,
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
                border = BorderStroke(
                    width = 2.dp,
                    color = dialogColors.border.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box {
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_close_24),
                            contentDescription = "Close dialog",
                            modifier = Modifier
                                .height(18.dp)
                                .width(18.dp),
                            colorFilter = ColorFilter.tint(dialogColors.content),
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp, bottom = 20.dp),
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
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (secondaryActionText != null && onSecondaryAction != null) {
                                EchoOutlinedButton(
                                    onClick = {
                                        onSecondaryAction()
                                        onDismissRequest()
                                    },
                                    modifier = Modifier.wrapContentHeight(),
                                ) {
                                    Text(
                                        text = secondaryActionText,
                                        style = EchoTheme.typography.bodyLarge,
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            EchoFilledButton(
                                onClick = {
                                    onPrimaryAction()
                                    onDismissRequest()
                                },
                                variant = if (destructive) {
                                    ButtonVariant.Error
                                } else {
                                    ButtonVariant.Primary
                                },
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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoActionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    actionsAlignment: Alignment.Horizontal = Alignment.End,
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
                border = BorderStroke(
                    width = 2.dp,
                    color = dialogColors.border.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box {
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_close_24),
                            contentDescription = "Close dialog",
                            modifier = Modifier
                                .height(18.dp)
                                .width(18.dp),
                            colorFilter = ColorFilter.tint(dialogColors.content),
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp, bottom = 20.dp),
                    ) {
                        ProvideTextStyle(
                            value = EchoTheme.typography.headlineMedium.copy(
                                color = dialogColors.title,
                            ),
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
                            value = EchoTheme.typography.bodyLarge.copy(
                                color = dialogColors.content
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(
                                    12.dp,
                                    actionsAlignment
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
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

@Preview
@Composable
private fun PreviewLightEchoActionDialog() {
    EchoTheme {
        EchoActionDialog(
            onDismissRequest = {},
            title = "Delete item",
            content = "Are you sure you want to delete this item? This action cannot be undone.",
            primaryActionText = "Delete",
            onPrimaryAction = {},
            secondaryActionText = "Cancel",
            onSecondaryAction = {},
            destructive = true,
        )
    }
}

@Preview
@Composable
private fun PreviewDarkEchoActionDialog() {
    EchoTheme(isDarkTheme = true) {
        EchoActionDialog(
            onDismissRequest = {},
            title = "Add item",
            content = "Are you sure you want to add this item? This action cannot be undone.",
            primaryActionText = "Add",
            onPrimaryAction = {},
            secondaryActionText = "Cancel",
            onSecondaryAction = {},
            destructive = false,
        )
    }
}