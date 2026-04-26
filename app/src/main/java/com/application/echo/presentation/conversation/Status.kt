package com.application.echo.presentation.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha30
import com.application.echo.ui.design.utils.alpha70


@Composable
fun MyStatus(
    initial: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClick)
                    .background(EchoTheme.colorScheme.surface.highest)
            ) {
                Text(
                    text = initial,
                    style = EchoTheme.typography.titleMedium,
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(EchoTheme.colorScheme.surface.highest)
                    .border(1.5.dp, EchoTheme.colorScheme.surface.color, CircleShape)
                    .align(Alignment.BottomEnd),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = EchoTheme.colorScheme.surface.onColor,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Text(
            text = label,
            style = EchoTheme.typography.labelMedium,
            color = EchoTheme.colorScheme.surface.onColor.alpha70,
        )
    }
}

@Composable
fun FriendStatus(
    initial: String,
    label: String,
    textColor: Color,
    isActive: Boolean = true,
) {
    val primaryColor = EchoTheme.colorScheme.primary.color
    val secondaryColor = EchoTheme.colorScheme.secondary.color

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .then(
                    if (isActive) {
                        Modifier.drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        primaryColor,
                                        secondaryColor,
                                        primaryColor,
                                    )
                                ),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 2.dp.toPx()),
                            )
                        }
                    } else {
                        Modifier.border(
                            width = 2.dp,
                            color = EchoTheme.colorScheme.surface.onColor.alpha30,
                            shape = CircleShape,
                        )
                    }
                )
                .padding(4.dp)
                .clip(CircleShape)
                .background(textColor.copy(alpha = 0.3f)),
        ) {
            Text(
                text = initial,
                style = EchoTheme.typography.titleMedium,
                color = textColor,
            )
        }

        Text(
            text = label,
            style = EchoTheme.typography.labelMedium,
            color = EchoTheme.colorScheme.surface.onColor.alpha70,
        )
    }
}

internal data class Friend(
    val initial: String,
    val label: String,
    val textColor: Color,
    val isActive: Boolean = true,
)

internal val sampleFriends = listOf(
    Friend(initial = "A", label = "Amaya",  textColor = Color(0xFFFF5E5E), isActive = true),
    Friend(initial = "D", label = "Dev",    textColor = Color(0xFF93C5FD), isActive = true),
    Friend(initial = "L", label = "Liora",  textColor = Color(0xFF6EE7B7), isActive = false),
    Friend(initial = "M", label = "Marco",  textColor = Color(0xFFFFAB91), isActive = true),
    Friend(initial = "S", label = "Sofia",  textColor = Color(0xFFC4B5FD), isActive = false),
    Friend(initial = "J", label = "Jordan", textColor = Color(0xFFFCD34D), isActive = true),
)
