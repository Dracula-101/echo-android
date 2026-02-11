package com.application.echo.ui.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.application.echo.ui.components.button.EchoFilledButton
import com.application.echo.ui.components.button.EchoOutlinedButton
import com.application.echo.ui.components.spacing.EchoSpacer
import com.application.echo.ui.components.spacing.EchoSpacerSize
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.ToComposable
import com.application.echo.ui.design.theme.EchoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoIconActionDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    icon: IconResource? = null,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    onPositiveButtonClick: (() -> Unit)? = null,
    onNegativeButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val dialogColors = EchoTheme.colorScheme.dialogColors()

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp, bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    EchoSpacer(size = EchoSpacerSize.Small)

                    icon?.ToComposable(
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally),
                        color = EchoTheme.colorScheme.primary.color
                    )

                    Text(
                        text = title,
                        style = EchoTheme.typography.headlineMedium,
                        color = dialogColors.title,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    EchoSpacer(size = EchoSpacerSize.Small)

                    Text(
                        text = message,
                        style = EchoTheme.typography.titleMedium,
                        color = dialogColors.content.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    EchoSpacer()

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (negativeButtonText != null && onNegativeButtonClick != null) {
                            EchoOutlinedButton(
                                onClick = {
                                    onNegativeButtonClick()
                                    onDismissRequest()
                                },
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
                                onClick = {
                                    onPositiveButtonClick()
                                    onDismissRequest()
                                },
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
        }
    }
}
