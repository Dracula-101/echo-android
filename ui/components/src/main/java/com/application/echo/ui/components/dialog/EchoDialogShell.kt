package com.application.echo.ui.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.application.echo.ui.components.R
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Shared dialog chrome used by all Echo dialog variants.
 *
 * Renders a [BasicAlertDialog] → [Card] → close button + content column.
 *
 * @param compact When `true` uses tighter padding (14×12 dp), otherwise 24×24/20 dp.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EchoDialogShell(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    showCloseButton: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val dialogColors = EchoTheme.colorScheme.dialogColors()

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = EchoTheme.spacing.padding.large),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = EchoTheme.shapes.dialog,
                colors = EchoTheme.colorScheme.dialogCardColors(),
                border = BorderStroke(
                    width = EchoTheme.dimen.border.medium,
                    color = dialogColors.border.copy(alpha = 0.5f),
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Box {
                    if (showCloseButton) {
                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(if (compact) 0.dp else 12.dp),
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_close_24),
                                contentDescription = "Close dialog",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(dialogColors.content),
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = if (compact) 14.dp else EchoTheme.spacing.padding.large,
                                vertical = if (compact) 12.dp else 0.dp,
                            )
                            .then(
                                if (!compact) Modifier.padding(top = EchoTheme.spacing.padding.large, bottom = 20.dp)
                                else Modifier
                            ),
                        content = content,
                    )
                }
            }
        }
    }
}
