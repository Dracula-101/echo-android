package com.application.echo.ui.components.badge

import androidx.compose.foundation.background
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

@Composable
fun EchoBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Primary,
    icon: IconResource? = null,
) {
    val backgroundColor = when (variant) {
        BadgeVariant.Primary -> EchoTheme.colorScheme.primary.color
        BadgeVariant.Secondary -> EchoTheme.colorScheme.secondary.color
        BadgeVariant.Error -> EchoTheme.colorScheme.error.color
    }
    Box(
        modifier = modifier
            .padding(vertical = EchoTheme.spacing.padding.extraSmall)
            .clip(RoundedCornerShape(12.dp))
            .background(color = backgroundColor.copy(alpha = 0.7f))
            .padding(
                horizontal = EchoTheme.spacing.padding.small,
                vertical = EchoTheme.spacing.padding.extraSmall,
            ),
    ) {
        ProvideTextStyle(EchoTheme.typography.labelMedium) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.padding.extraSmall),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .then(
                        if (icon != null) Modifier.padding(end = EchoTheme.spacing.padding.extraSmall)
                        else Modifier
                    )
            ) {
                icon?.ToComposable(
                    modifier = Modifier
                        .size(EchoTheme.typography.labelMedium.fontSize.value.dp * 1.5f)
                )
                Text(
                    text = text,
                    style = EchoTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (variant) {
                        BadgeVariant.Primary -> EchoTheme.colorScheme.primary.onColor
                        BadgeVariant.Secondary -> EchoTheme.colorScheme.secondary.onColor
                        BadgeVariant.Error -> EchoTheme.colorScheme.error.onColor
                    }
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun EchoBadgePreview() {
    EchoTheme {
        Surface {
            Column (
                modifier = Modifier.padding(16.dp),
            ){
                EchoBadge(text = "Primary", variant = BadgeVariant.Primary)
                EchoBadge(text = "Secondary", variant = BadgeVariant.Secondary)
                EchoBadge(text = "Error", variant = BadgeVariant.Error)
            }
        }
    }
}


