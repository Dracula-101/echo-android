package com.application.echo.presentation.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.application.echo.features.messaging.model.OnlineStatus
import com.application.echo.ui.design.theme.EchoTheme

@Composable
fun EchoAvatar(
    name: String,
    avatarUrl: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    onlineStatus: OnlineStatus? = null,
) {
    Box(modifier = modifier) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = name,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            val fontSize = when {
                size <= 32.dp -> 13.sp
                size <= 44.dp -> 16.sp
                size <= 56.dp -> 20.sp
                else -> 28.sp
            }
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(EchoTheme.colorScheme.primary.container),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "?",
                    fontSize = fontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = EchoTheme.colorScheme.primary.onContainer,
                )
            }
        }

        if (onlineStatus != null) {
            val dotSize = when {
                size <= 32.dp -> 10.dp
                size <= 44.dp -> 12.dp
                else -> 14.dp
            }
            val innerDotSize = dotSize - 4.dp
            PresenceDot(
                onlineStatus = onlineStatus,
                dotSize = dotSize,
                innerDotSize = innerDotSize,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(1.dp),
            )
        }
    }
}

@Composable
private fun PresenceDot(
    onlineStatus: OnlineStatus,
    dotSize: Dp,
    innerDotSize: Dp,
    modifier: Modifier = Modifier,
) {
    val color = when (onlineStatus) {
        OnlineStatus.ONLINE -> Color(0xFF31C26A)
        OnlineStatus.AWAY -> Color(0xFFFFB020)
        OnlineStatus.OFFLINE -> EchoTheme.colorScheme.outline.color.copy(alpha = 0.5f)
        OnlineStatus.UNKNOWN -> return
    }

    Box(
        modifier = modifier
            .size(dotSize)
            .clip(CircleShape)
            .background(EchoTheme.colorScheme.background.color),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(innerDotSize)
                .clip(CircleShape)
                .background(color),
        )
    }
}

@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "typing")

    val dot1 by transition.animateFloat(
        initialValue = 0f, targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot1",
    )
    val dot2 by transition.animateFloat(
        initialValue = 0f, targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 133, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot2",
    )
    val dot3 by transition.animateFloat(
        initialValue = 0f, targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 266, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot3",
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(dot1, dot2, dot3).forEach { offset ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .offset(y = offset.dp)
                    .clip(CircleShape)
                    .background(EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.5f)),
            )
        }
    }
}
