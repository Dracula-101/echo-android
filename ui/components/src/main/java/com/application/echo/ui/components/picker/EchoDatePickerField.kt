package com.application.echo.ui.components.picker

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.textfield.textFieldColors
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha70
import java.util.Calendar

private const val BORDER_ANIM_DURATION_MS = 180

data class EchoDate(
    val day: Int,
    val month: Int,
    val year: Int,
)

fun Long.toEchoDate(): EchoDate {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@toEchoDate
    }

    return EchoDate(
        day = calendar.get(Calendar.DAY_OF_MONTH),
        month = calendar.get(Calendar.MONTH) + 1,
        year = calendar.get(Calendar.YEAR),
    )
}

fun EchoDate.toMillis(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoDatePickerField(
    selectedDate: EchoDate?,
    onDateSelected: (EchoDate) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    helperText: String? = null,
    errorText: String? = null,
    boxHeight: Dp = EchoTheme.dimen.height.large,
    boxSpacing: Dp = EchoTheme.spacing.gap.small,
    boxShape: Shape = EchoTheme.shapes.input,
    boxPadding: PaddingValues = PaddingValues(
        horizontal = EchoTheme.spacing.padding.medium,
        vertical = EchoTheme.spacing.padding.extraSmall,
    ),
    valueTextStyle: TextStyle = EchoTheme.typography.bodyLarge,
    cancelText: String = "Cancel",
    doneText: String = "Done",
) {
    val colors = EchoTheme.colorScheme.textFieldColors()
    var showDialog by remember { mutableStateOf(false) }

    val day = selectedDate?.day?.toString()?.padStart(2, '0') ?: ""
    val month = selectedDate?.month?.toString()?.padStart(2, '0') ?: ""
    val year = selectedDate?.year?.toString() ?: ""

    Column(modifier = modifier) {
        if (label != null) {
            label()
            Spacer(Modifier.height(EchoTheme.spacing.gap.extraSmall))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(boxSpacing),
        ) {
            EchoDateBox(
                value = day,
                enabled = enabled,
                isError = isError,
                modifier = Modifier.weight(1f),
                height = boxHeight,
                shape = boxShape,
                padding = boxPadding,
                valueTextStyle = valueTextStyle,
                onClick = { showDialog = true },
            )

            EchoDateBox(
                value = month,
                enabled = enabled,
                isError = isError,
                modifier = Modifier.weight(1f),
                height = boxHeight,
                shape = boxShape,
                padding = boxPadding,
                valueTextStyle = valueTextStyle,
                onClick = { showDialog = true },
            )

            EchoDateBox(
                value = year,
                enabled = enabled,
                isError = isError,
                modifier = Modifier.weight(1f),
                height = boxHeight,
                shape = boxShape,
                padding = boxPadding,
                valueTextStyle = valueTextStyle,
                onClick = { showDialog = true },
            )
        }

        val bottomText = if (isError) errorText else helperText
        if (bottomText != null) {
            Spacer(Modifier.height(EchoTheme.spacing.gap.extraSmall))
            Text(
                text = bottomText,
                style = EchoTheme.typography.labelSmall,
                color = if (isError) colors.errorText else colors.helperText,
            )
        }
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.toMillis(),
        )

        Box(
            modifier = Modifier
                .padding(EchoTheme.spacing.padding.large)
        ) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                colors = DatePickerDefaults.colors(
                    containerColor = EchoTheme.colorScheme.surface.color,
                ),
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                onDateSelected(millis.toEchoDate())
                            }
                            showDialog = false
                        },
                    ) {
                        Text(
                            text = doneText,
                            color = EchoTheme.colorScheme.primary.color,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(
                            text = cancelText,
                            color = EchoTheme.colorScheme.surface.onColor.alpha70,
                        )
                    }
                },
            ) {
                DatePicker(
                    state = datePickerState,
                    title = {
                        Text(
                            text = "Select date",
                            style = EchoTheme.typography.titleMedium,
                            color = EchoTheme.colorScheme.surface.onColor,
                            modifier = Modifier.padding(
                                start = EchoTheme.spacing.padding.medium,
                                top = EchoTheme.spacing.padding.medium,
                            ),
                        )
                    },
                    headline = {
                        Text(
                            text = selectedDate?.let {
                                "${it.day.toString().padStart(2, '0')}/${it.month.toString().padStart(2, '0')}/${it.year}"
                            } ?: "Choose your date",
                            style = EchoTheme.typography.headlineSmall,
                            color = EchoTheme.colorScheme.primary.color,
                            modifier = Modifier.padding(horizontal = EchoTheme.spacing.padding.medium),
                        )
                    },
                    colors = DatePickerDefaults.colors(
                        containerColor = EchoTheme.colorScheme.surface.color,
                        titleContentColor = EchoTheme.colorScheme.surface.onColor,
                        headlineContentColor = EchoTheme.colorScheme.primary.color,
                        weekdayContentColor = EchoTheme.colorScheme.surface.onColor.alpha70,
                        subheadContentColor = EchoTheme.colorScheme.surface.onColor.alpha70,
                        yearContentColor = EchoTheme.colorScheme.surface.onColor,
                        currentYearContentColor = EchoTheme.colorScheme.primary.color,
                        selectedYearContentColor = EchoTheme.colorScheme.primary.onColor,
                        selectedYearContainerColor = EchoTheme.colorScheme.primary.color,
                        dayContentColor = EchoTheme.colorScheme.surface.onColor,
                        selectedDayContentColor = EchoTheme.colorScheme.primary.onColor,
                        selectedDayContainerColor = EchoTheme.colorScheme.primary.color,
                        todayContentColor = EchoTheme.colorScheme.primary.color,
                        todayDateBorderColor = EchoTheme.colorScheme.primary.color,
                        navigationContentColor = EchoTheme.colorScheme.surface.onColor,
                    ),
                )
            }
        }
    }
}

@Composable
private fun EchoDateBox(
    value: String,
    enabled: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier,
    height: Dp,
    shape: Shape,
    padding: PaddingValues,
    valueTextStyle: TextStyle,
    onClick: () -> Unit,
) {
    val colors = EchoTheme.colorScheme.textFieldColors()
    val interactionSource = remember { MutableInteractionSource() }

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledBorder
            isError -> colors.errorBorder
            else -> colors.unfocusedBorder
        },
        animationSpec = tween(durationMillis = BORDER_ANIM_DURATION_MS),
        label = "date_box_border_color",
    )

    val valueColor = when {
        !enabled -> colors.disabledText
        value.isBlank() -> colors.placeholder
        else -> colors.text
    }

    Surface(
        modifier = modifier
            .height(height)
            .clip(shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .border(
                border = BorderStroke(
                    width = EchoTheme.dimen.border.small,
                    color = borderColor,
                ),
                shape = shape,
            )
            .background(colors.background, shape),
        shape = shape,
        color = colors.background,
    ) {
        CompositionLocalProvider(LocalContentColor provides valueColor) {
            Column(
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = value,
                    style = valueTextStyle,
                    color = valueColor,
                )
            }
        }
    }
}