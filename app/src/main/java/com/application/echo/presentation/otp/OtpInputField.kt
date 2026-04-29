package com.application.echo.presentation.otp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme

@Composable
fun OtpInputField(
    digits: List<String>,
    isError: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onDigitChanged: (index: Int, digit: String) -> Unit,
    onPaste: (raw: String) -> Unit,
    onBackspace: (index: Int) -> Unit,
    onFilled: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val length    = digits.size
    val otpValue  = digits.joinToString("")
    val enabled   = !isLoading

    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }

    BasicTextField(
        value         = otpValue,
        onValueChange = { raw ->
            if (!enabled) return@BasicTextField
            val new = raw.filter { it.isDigit() }
            when {
                new.length > otpValue.length + 1 -> {
                    // paste: hand off the full sanitized string, VM caps at length
                    onPaste(new)
                }
                new.length == otpValue.length + 1 && new.length <= length -> {
                    onDigitChanged(otpValue.length, new.last().toString())
                    if (new.length == length) onFilled()
                }
                new.length < otpValue.length -> {
                    // new.length is now the index of the cleared slot
                    onBackspace(new.length)
                }
            }
        },
        enabled       = enabled,
        modifier      = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .alpha(if (isLoading) 0.5f else 1f),
        singleLine    = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction    = ImeAction.Done,
        ),
        cursorBrush = SolidColor(Color.Transparent),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.fillMaxWidth(),
            ) {
                repeat(length) { index ->
                    OtpDigitCell(
                        digit     = digits.getOrElse(index) { "" },
                        isError   = isError,
                        isFocused = otpValue.length == index && enabled,
                        modifier  = Modifier.weight(1f),
                    )
                }
            }
        },
    )
}

@Composable
private fun OtpDigitCell(
    digit: String,
    isError: Boolean,
    isFocused: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)

    val borderColor by animateColorAsState(
        targetValue = when {
            isError              -> EchoTheme.colorScheme.error.color
            isFocused            -> EchoTheme.colorScheme.primary.color
            digit.isNotEmpty()   -> EchoTheme.colorScheme.primary.color.copy(alpha = 0.5f)
            else                 -> EchoTheme.colorScheme.outline.color
        },
        animationSpec = tween(150),
        label         = "borderColor",
    )

    val borderWidth by animateDpAsState(
        targetValue   = if (isFocused || isError) 2.dp else 1.dp,
        animationSpec = tween(150),
        label         = "borderWidth",
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isError            -> EchoTheme.colorScheme.error.color.copy(alpha = 0.08f)
            isFocused          -> EchoTheme.colorScheme.primary.color.copy(alpha = 0.06f)
            digit.isNotEmpty() -> EchoTheme.colorScheme.surface.variant
            else               -> EchoTheme.colorScheme.surface.high
        },
        animationSpec = tween(150),
        label         = "backgroundColor",
    )

    Box(
        modifier         = modifier
            .height(56.dp)
            .clip(shape)
            .background(backgroundColor)
            .border(width = borderWidth, color = borderColor, shape = shape),
        contentAlignment = Alignment.Center,
    ) {
        if (digit.isEmpty()) {
            Text(
                text      = "•",
                style     = EchoTheme.typography.titleLarge,
                color     = EchoTheme.colorScheme.outline.color,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text       = digit,
                style      = EchoTheme.typography.headlineMedium,
                color      = if (isError) EchoTheme.colorScheme.error.color
                else EchoTheme.colorScheme.surface.onColor,
                textAlign  = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                maxLines   = 1,
            )
        }
    }
}