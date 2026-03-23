package com.application.echo.ui.components.snackbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.plus
import com.application.echo.ui.design.colors.EchoColorScheme
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private data class CardColors(
    val border    : Color,
    val container : Color,
    val content   : Color,
    val muted     : Color,
    val accent    : Color,
    val iconBg    : Color,
)

private fun EchoSnackbarType.cardColors(scheme: EchoColorScheme): CardColors {
    val accent = when (this) {
        EchoSnackbarType.INFO    -> scheme.primary.color
        EchoSnackbarType.SUCCESS -> scheme.secondary.color
        EchoSnackbarType.WARNING -> scheme.primary.dim
        EchoSnackbarType.ERROR   -> scheme.error.color
    }
    return CardColors(
        border    = accent.copy(alpha = 0.50f),
        container = scheme.surface.high,
        content   = scheme.surface.onColor,
        muted     = scheme.surface.onColor.copy(alpha = 0.50f),
        accent    = accent,
        iconBg    = accent.copy(alpha = 0.10f),
    )
}

private fun EchoSnackbarType.icon(): ImageVector = when (this) {
    EchoSnackbarType.INFO    -> Icons.Filled.Info
    EchoSnackbarType.SUCCESS -> Icons.Filled.CheckCircle
    EchoSnackbarType.WARNING -> Icons.Filled.Warning
    EchoSnackbarType.ERROR   -> Icons.Filled.Error
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EchoSnackbar(
    data: EchoSnackbarData,
    isFront: Boolean,
    onDismiss: () -> Unit,
    onSwipeDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors  = data.type.cardColors(EchoTheme.colorScheme)
    val typo    = EchoTheme.typography
    val scope   = rememberCoroutineScope()
    val density = LocalDensity.current

    val offsetX          = remember { Animatable(0f) }
    val dismissThreshold = with(density) { 110.dp.toPx() }
    val swipeFraction    = (offsetX.value / dismissThreshold).coerceIn(0f, 1f)

    LaunchedEffect(isFront) {
        if (!isFront) offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Card(
        modifier  = modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .alpha(if (isFront) 1f - swipeFraction * 0.65f else 1f)
            .pointerInput(isFront) {
                if (!isFront) return@pointerInput
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX.value >= dismissThreshold) {
                            scope.launch {
                                offsetX.animateTo(size.width * 1.5f, tween(160))
                                onSwipeDismiss()
                            }
                        } else {
                            scope.launch {
                                offsetX.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
                            }
                        }
                    },
                    onHorizontalDrag = { change, delta ->
                        change.consume()
                        scope.launch { offsetX.snapTo((offsetX.value + delta).coerceAtLeast(0f)) }
                    },
                )
            },
        shape     = EchoTheme.shapes.snackbar,
        colors    = CardDefaults.cardColors(containerColor = colors.container),
        border    = BorderStroke(EchoTheme.dimen.border.medium, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(EchoTheme.spacing.padding.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier         = Modifier
                    .size(28.dp)
                    .background(colors.iconBg, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = data.type.icon(),
                    contentDescription = null,
                    tint               = colors.accent,
                    modifier           = Modifier.size(EchoTheme.dimen.icon.small),
                )
            }

            Column {
                Row {
                    Text(
                        text  = data.message,
                        color = colors.content,
                        style = typo.titleSmall,
                        modifier = Modifier.weight(1f),
                    )

                    if (data.actionLabel != null) {
                        TextButton(
                            onClick        = { data.onAction?.invoke(); onDismiss() },
                            contentPadding = PaddingValues(horizontal = 8.dp),
                        ) {
                            Text(
                                text  = data.actionLabel,
                                color = colors.accent,
                                style = typo.labelLarge,
                            )
                        }
                    }
                }

                if (data.detail != null || data.code != null) {
                    Text(
                        buildAnnotatedString {
                            if (data.detail != null) {
                                pushStyle(typo.bodyMedium.toSpanStyle().copy(color = colors.muted))
                                append(data.detail)
                            }
                            if (data.code != null) {
                                pushStyle(typo.labelMedium.toSpanStyle().copy(color = colors.accent, fontWeight = FontWeight.ExtraBold))
                                append(" [${data.code}]")
                            }
                        },
                    )
                }
            }
        }
    }
}
