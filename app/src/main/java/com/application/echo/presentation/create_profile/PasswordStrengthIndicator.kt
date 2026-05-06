package com.application.echo.presentation.create_profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha10


@Composable
fun PasswordStrengthIndicator(
    password: String,
    modifier: Modifier = Modifier,
    passwordStrength: PasswordStrength? = null,
) {
    val strength = passwordStrength ?: password.passwordStrength()
    val activeBars = strength.barCount()

    val activeColor = when (strength) {
        PasswordStrength.Empty -> EchoTheme.colorScheme.surface.onColor.alpha10
        PasswordStrength.Weak -> EchoTheme.colorScheme.error.color
        PasswordStrength.Medium -> EchoTheme.colorScheme.secondary.color
        PasswordStrength.Strong -> EchoTheme.colorScheme.success.color
    }

    val inactiveColor = EchoTheme.colorScheme.surface.onColor.alpha10

    Column {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.extraSmall),
        ) {
            repeat(4) { index ->
                val color by animateColorAsState(
                    targetValue = if (index < activeBars) activeColor else inactiveColor,
                    animationSpec = tween(180),
                    label = "password_strength_bar",
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            color = color,
                            shape = RoundedCornerShape(50),
                        ),
                )
            }
        }
        Text(
            text = strength.toString(),
            style = EchoTheme.typography.labelMedium,
            color = activeColor,
            modifier = Modifier.padding(top = EchoTheme.spacing.gap.extraSmall),
        )
    }
}