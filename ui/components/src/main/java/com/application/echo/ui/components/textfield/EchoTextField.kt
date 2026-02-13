package com.application.echo.ui.components.textfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.times
import com.application.echo.ui.design.theme.EchoTheme

private const val BORDER_ANIM_DURATION_MS = 200

/**
 * Themed text field with label, placeholder, error, and helper text support.
 *
 * Uses the design system's shapes, spacing, and colors for a consistent look.
 *
 * ```kotlin
 * EchoTextField(
 *     value = email,
 *     onValueChange = { email = it },
 *     label = "Email",
 *     placeholder = "you@example.com",
 * )
 *
 * EchoTextField(
 *     value = password,
 *     onValueChange = { password = it },
 *     label = "Password",
 *     isError = passwordError != null,
 *     errorText = passwordError,
 *     trailing = { IconButton(onClick = ::toggleVisibility) { ... } },
 * )
 * ```
 */
@Composable
fun EchoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    isError: Boolean = errorText != null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = EchoTheme.colorScheme.textFieldColors()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledBorder
            isError -> colors.errorBorder
            isFocused -> colors.focusedBorder
            else -> colors.unfocusedBorder
        },
        animationSpec = tween(durationMillis = BORDER_ANIM_DURATION_MS),
        label = "border_color",
    )

    Column(modifier = modifier) {
        // ── Label ──
        if (label != null) {
            Text(
                text = label,
                style = EchoTheme.typography.labelMedium,
                color = when {
                    !enabled -> colors.disabledText
                    isError -> colors.errorText
                    else -> colors.label
                },
            )
            Spacer(Modifier.height(EchoTheme.spacing.gap.extraSmall))
        }

        // ── Input row ──
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = EchoTheme.typography.bodyLarge.copy(
                color = if (enabled) colors.text else colors.disabledText,
            ),
            cursorBrush = SolidColor(colors.cursor),
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(EchoTheme.shapes.input)
                        .border(
                            border = BorderStroke(
                                width = EchoTheme.dimen.border.medium,
                                color = borderColor,
                            ),
                            shape = EchoTheme.shapes.input,
                        )
                        .padding(
                            horizontal = EchoTheme.spacing.padding.medium,
                            vertical = 1.2 * EchoTheme.spacing.padding.small,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leading != null) {
                        CompositionLocalProvider(
                            LocalContentColor provides colors.placeholder,
                        ) {
                            leading()
                        }
                        Spacer(Modifier.padding(start = EchoTheme.spacing.gap.small))
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                style = EchoTheme.typography.bodyLarge,
                                color = colors.placeholder,
                            )
                        }
                        innerTextField()
                    }

                    if (trailing != null) {
                        Spacer(Modifier.padding(end = EchoTheme.spacing.gap.small))
                        CompositionLocalProvider(
                            LocalContentColor provides colors.placeholder,
                        ) {
                            trailing()
                        }
                    }
                }
            },
        )

        // ── Helper / Error text ──
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
}
