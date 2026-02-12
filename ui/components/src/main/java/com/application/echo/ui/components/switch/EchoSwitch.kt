package com.application.echo.ui.components.switch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Custom toggle switch with smooth thumb animation.
 *
 * ```kotlin
 * var darkMode by remember { mutableStateOf(false) }
 * EchoSwitch(
 *     checked = { darkMode },
 *     onCheckedChange = { darkMode = it },
 * )
 * ```
 *
 * @param checked Lambda returning the current checked state (deferred read).
 * @param onCheckedChange Called when the user taps the switch.
 * @param variant Color variant for the checked track/thumb.
 */
@Composable
fun EchoSwitch(
    checked: () -> Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: EchoVariant = EchoVariant.Primary,
    width: Dp = 42.dp,
    height: Dp = 24.dp,
    thumbSize: Dp = 20.dp,
    thumbPadding: Dp = 2.dp,
) {
    val isChecked = checked()
    val colors = EchoTheme.colorScheme.switchColors(variant)

    val trackRadius = height / 2
    val thumbTravel = width - thumbSize - (thumbPadding * 2)

    val thumbOffset by animateFloatAsState(
        targetValue = if (isChecked) (thumbPadding + thumbTravel).value else thumbPadding.value,
        animationSpec = tween(durationMillis = 150),
        label = "thumb_position",
    )

    val trackColor = when {
        !enabled -> if (isChecked) colors.disabledCheckedTrack else colors.disabledUncheckedTrack
        isChecked -> colors.checkedTrack
        else -> colors.uncheckedTrack
    }

    val thumbColor = when {
        !enabled -> if (isChecked) colors.disabledCheckedThumb else colors.disabledUncheckedThumb
        isChecked -> colors.checkedThumb
        else -> colors.uncheckedThumb
    }

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(trackRadius))
            .background(trackColor)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                role = Role.Switch,
                onClick = { onCheckedChange(!isChecked) },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset.dp)
                .size(thumbSize)
                .clip(CircleShape)
                .background(thumbColor),
        )
    }
}
