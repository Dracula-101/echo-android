package com.application.echo.ui.components.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.application.echo.ui.design.theme.EchoTheme
import kotlinx.coroutines.delay

private const val EXIT_ANIM_MS  = 240L
private const val STACK_PEEK_DP = 7f
private const val STACK_SCALE   = 0.04f
private const val STACK_ALPHA   = 0.20f

@Composable
fun EchoSnackbarHost(
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
                val activeEntries = state.entries.filter { !it.isExiting }
                val depth   = if (entry.isExiting) 0 else activeEntries.indexOf(entry)
                val isFront = depth == 0 && !entry.isExiting

                EchoSnackbarItem(
                    entry          = entry,
                    depth          = depth,
                    isFront        = isFront,
                    onDismiss      = { state.startExit(entry.data.id) },
                    onSwipeDismiss = { state.removeEntry(entry.data.id) },
                    onExited       = { state.removeEntry(entry.data.id) },
                )
            }
        }
    }
}

@Composable
private fun EchoSnackbarItem(
    entry: EchoSnackbarEntry,
    depth: Int,
    isFront: Boolean,
    onDismiss: () -> Unit,
    onSwipeDismiss: () -> Unit,
    onExited: () -> Unit,
) {
    // Enter animation fix: start invisible so AnimatedVisibility
    // sees the false → true transition and plays the enter spec.
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(entry.isExiting) {
        if (entry.isExiting) {
            delay(EXIT_ANIM_MS)
            onExited()
        }
    }

    val animScale by animateFloatAsState(
        targetValue   = 1f - depth * STACK_SCALE,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label         = "EchoScale",
    )
    val animAlpha by animateFloatAsState(
        targetValue   = if (entry.isExiting) 1f else (1f - depth * STACK_ALPHA),
        animationSpec = tween(durationMillis = 130),
        label         = "EchoAlpha",
    )
    val animOffsetDp by animateFloatAsState(
        targetValue   = depth * STACK_PEEK_DP,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label         = "EchoOffset",
    )

    AnimatedVisibility(
        visible  = visible && !entry.isExiting,
        enter    = slideInVertically(
            animationSpec  = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
            initialOffsetY = { it },
        ) + fadeIn(tween(durationMillis = 200)),
        exit     = slideOutVertically(
            animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
            targetOffsetY = { it },
        ) + fadeOut(tween(durationMillis = 160)),
        modifier = Modifier
            .zIndex(if (entry.isExiting) 4f else (4 - depth).toFloat())
            .padding(start = EchoTheme.spacing.padding.medium, end = EchoTheme.spacing.padding.medium, bottom = EchoTheme.spacing.padding.large)
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX          = animScale
                scaleY          = animScale
                transformOrigin = TransformOrigin(0.5f, 1f)
                translationY    = -(animOffsetDp.dp.toPx())
                alpha           = if (!entry.isExiting) animAlpha else 1f
            },
    ) {
        EchoSnackbar(
            data           = entry.data,
            isFront        = isFront,
            onDismiss      = onDismiss,
            onSwipeDismiss = onSwipeDismiss,
        )
    }
}

@Composable
fun rememberEchoSnackbarState(): EchoSnackbarState = remember { EchoSnackbarState() }
