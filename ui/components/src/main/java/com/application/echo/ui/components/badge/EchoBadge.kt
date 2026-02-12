package com.application.echo.ui.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.application.echo.ui.components.common.EchoVariant
import com.application.echo.ui.components.common.color
import com.application.echo.ui.components.common.onColor
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Badge style — controls the visual treatment of the badge background.
 */
enum class EchoBadgeStyle {
    /** Solid semi-transparent fill. */
    Filled,
    /** Outline border with a subtle fill. */
    Outlined,
}

/**
 * Compact label chip for status, category, or count display.
 *
 * ```kotlin
 * EchoBadge(text = "New")
 * EchoBadge(text = "Live", variant = EchoVariant.Error, style = EchoBadgeStyle.Outlined)
 * EchoBadge(text = "3 unread", icon = IconResource.Vector(Icons.Default.Mail))
 * ```
 */
@Composable
fun EchoBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: EchoVariant = EchoVariant.Primary,
    style: EchoBadgeStyle = EchoBadgeStyle.Filled,
    icon: IconResource? = null,
) {
    val accent = variant.color()
    val chipShape = EchoTheme.shapes.chip
    val isFilled = style == EchoBadgeStyle.Filled

    val backgroundModifier = if (isFilled) {
        Modifier.background(color = accent.copy(alpha = 0.7f))
    } else {
        Modifier
            .border(
                width = EchoTheme.dimen.divider.small,
                color = accent,
                shape = chipShape,
            )
            .background(color = accent.copy(alpha = 0.15f))
    }

    Row(
        modifier = modifier
            .clip(chipShape)
            .then(backgroundModifier)
            .padding(
                horizontal = EchoTheme.spacing.padding.small,
                vertical = EchoTheme.spacing.padding.extraSmall,
            ),
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon.Paint(
                modifier = Modifier.size(EchoTheme.dimen.icon.extraSmall),
                color = if (isFilled) variant.onColor() else accent,
            )
        }
        Text(
            text = text,
            style = EchoTheme.typography.labelMedium,
            fontWeight = if (isFilled) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isFilled) variant.onColor() else accent,
        )
    }
}
