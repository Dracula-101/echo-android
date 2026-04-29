package com.application.echo.ui.components.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.application.echo.ui.design.colors.EchoColorScheme

private const val FLAT_EXIT_ANIM_MS = 220L

private fun EchoSnackbarType.flatColors(scheme: EchoColorScheme): FlatCardColors {
    val accent = when (this) {
        EchoSnackbarType.INFO    -> scheme.primary.color
        EchoSnackbarType.SUCCESS -> scheme.secondary.color
        EchoSnackbarType.WARNING -> scheme.primary.dim
        EchoSnackbarType.ERROR   -> scheme.error.color
    }
    return FlatCardColors(
        container = scheme.surface.high,
        content   = scheme.surface.onColor,
        accent    = accent,
        iconBg    = accent.copy(alpha = 0.10f),
    )
}

private fun EchoSnackbarType.flatIcon(): ImageVector = when (this) {
    EchoSnackbarType.INFO    -> Icons.Filled.Info
    EchoSnackbarType.SUCCESS -> Icons.Filled.CheckCircle
    EchoSnackbarType.WARNING -> Icons.Filled.Warning
    EchoSnackbarType.ERROR   -> Icons.Filled.Error
}

private data class FlatCardColors(
    val container : Color,
    val content   : Color,
    val accent    : Color,
    val iconBg    : Color,
)

@Composable
fun EchoFlatSnackbarHost(
    state: EchoSnackbarState,
    modifier: Modifier = Modifier,
) {
    val frontId = state.entries.firstOrNull { !it.isExiting }?.data?.id

    LaunchedEffect(frontId) {
        val front = state.entries.firstOrNull { it.data.id == frontId } ?: return@LaunchedEffect
        if (front.data.duration is EchoSnackbarDuration.Indefinite) return@LaunchedEffect
        delay(front.data.duration.millis)
        state.startExit(front.data.id)
    }

    Box(
        modifier         = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        state.entries.forEach { entry ->
            key(entry.data.id) {
                EchoFlatSnackbarItem(
                    entry    = entry,
                    onExited = { state.removeEntry(entry.data.id) },
                )
            }
        }
    }
}

@Composable
private fun EchoFlatSnackbarItem(
    entry: EchoSnackbarEntry,
    onExited: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(entry.isExiting) {
        if (entry.isExiting) {
            delay(FLAT_EXIT_ANIM_MS)
            onExited()
        }
    }

    AnimatedVisibility(
        visible  = visible && !entry.isExiting,
        enter    = slideInVertically(
            animationSpec  = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
            initialOffsetY = { it },
        ) + fadeIn(tween(durationMillis = 180)),
        exit     = slideOutVertically(
            animationSpec  = tween(durationMillis = 200, easing = FastOutLinearInEasing),
            targetOffsetY  = { it },
        ) + fadeOut(tween(durationMillis = 160)),
        modifier = Modifier
            .zIndex(1f)
            .fillMaxWidth(),
    ) {
        EchoFlatSnackbar(data = entry.data)
    }
}

@Composable
private fun EchoFlatSnackbar(
    data: EchoSnackbarData,
    modifier: Modifier = Modifier,
) {
    val colors = data.type.flatColors(EchoTheme.colorScheme)
    val typo   = EchoTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.container)
            .navigationBarsPadding()
            .padding(
                horizontal = EchoTheme.spacing.padding.medium,
                vertical   = EchoTheme.spacing.padding.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier         = Modifier
                .size(28.dp)
                .background(colors.iconBg, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = data.type.flatIcon(),
                contentDescription = null,
                tint               = colors.accent,
                modifier           = Modifier.size(EchoTheme.dimen.icon.small),
            )
        }

        Spacer(Modifier.width(EchoTheme.spacing.gap.small))

        Text(
            text     = data.message,
            color    = colors.content,
            style    = typo.bodyMedium,
            maxLines = 1,
        )
    }
}