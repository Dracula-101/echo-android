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
import com.application.echo.ui.design.theme.EchoTheme

/**
 * A modern switch component that can be used to toggle between two states.
 *
 * @param checked Function that returns the current checked state
 * @param onCheckedChange Function called when the switch state should change
 * @param modifier Modifier to be applied to the switch
 * @param enabled Whether the switch is enabled for interaction
 * @param interactionSource The MutableInteractionSource representing the stream of interactions
 */
@Composable
fun EchoSwitch(
    checked: () -> Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    width: Dp = 42.dp,
    height: Dp = 24.dp,
    thumbSize: Dp = 20.dp,
    thumbPadding: Dp = 2.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isChecked = checked()
    val colors = EchoTheme.colorScheme.switchColors()

    val trackRadius = height / 2
    val thumbTravel = width - thumbSize - (thumbPadding * 2)

    val thumbOffset by animateFloatAsState(
        targetValue = if (isChecked) (thumbPadding + thumbTravel).value else thumbPadding.value,
        animationSpec = tween(durationMillis = 150),
        label = "thumb_position"
    )

    val trackColor = when {
        !enabled -> if (isChecked) colors.disabledCheckedTrack else colors.disabledThumb
        isChecked -> colors.trackPressed
        else -> colors.track
    }

    val thumbColor = when {
        !enabled -> if (isChecked) colors.disabledCheckedThumb else colors.disabledTrack
        isChecked -> colors.thumbPressed
        else -> colors.thumb
    }

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(trackRadius))
            .background(trackColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Switch,
                    onClick = { onCheckedChange(!isChecked) }
                )
                .size(thumbSize)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}
