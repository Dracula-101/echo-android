package com.application.echo.ui.components.badge

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.ToComposable
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.theme.LocalEchoColorScheme

@Composable
fun EchoOutlineBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Primary,
    icon: IconResource? = null,
) {
    val outlineColor = when (variant) {
        BadgeVariant.Primary -> EchoTheme.colorScheme.primary.color
        BadgeVariant.Secondary -> EchoTheme.colorScheme.secondary.color
        BadgeVariant.Error -> EchoTheme.colorScheme.error.color
    }

    Box(
        modifier = modifier
            .padding(vertical = EchoTheme.spacing.padding.extraSmall)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = EchoTheme.dimen.divider.small,
                color = outlineColor,
                shape = RoundedCornerShape(12.dp),
            )
            .background(color = outlineColor.copy(alpha = 0.2f))
            .padding(
                horizontal = EchoTheme.spacing.padding.small,
                vertical = EchoTheme.spacing.padding.extraSmall,
            ),
    ) {
        ProvideTextStyle(EchoTheme.typography.labelMedium) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.extraSmall),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .then(
                        if (icon != null) Modifier.padding(end = EchoTheme.spacing.padding.extraSmall)
                        else Modifier
                    )
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier.size(EchoTheme.typography.labelMedium.fontSize.value.dp * 1.5f),
                    ) {
                        icon.ToComposable()
                    }
                }
                Text(
                    text = text,
                    style = EchoTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = outlineColor,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun EchoOutlineBadgePreview() {
    EchoTheme {
        Surface {
            Column(modifier = Modifier.padding(EchoTheme.spacing.padding.medium)) {
                EchoOutlineBadge(
                    text = "Primary",
                    variant = BadgeVariant.Primary,
                )
                EchoOutlineBadge(
                    text = "Secondary",
                    variant = BadgeVariant.Secondary,
                )
                EchoOutlineBadge(
                    text = "Error",
                    variant = BadgeVariant.Error,
                )
            }
        }
    }
}