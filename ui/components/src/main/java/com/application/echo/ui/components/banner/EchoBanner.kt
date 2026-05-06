package com.application.echo.ui.components.banner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.application.echo.ui.components.util.IconResource
import com.application.echo.ui.components.util.Paint
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Inline informational banner with a leading icon, bold title, and optional body.
 *
 * Banners persist until the user scrolls past them or explicitly dismisses them —
 * use [com.application.echo.ui.components.snackbar.EchoSnackbar] for transient feedback instead.
 *
 * ```kotlin
 * EchoBanner(
 *     title = "End-to-end encrypted",
 *     description = "Only you and Amaya can read these messages.",
 *     tone = BannerTone.Success,
 * )
 * ```
 *
 * @param title Bold headline rendered next to the icon.
 * @param tone Semantic tone — see [BannerTone].
 * @param description Optional muted body text below the title.
 * @param icon Override for the default tone icon.
 */
@Composable
fun EchoBanner(
    title: String,
    modifier: Modifier = Modifier,
    tone: BannerTone = BannerTone.Info,
    description: String? = null,
    icon: IconResource? = null,
) {
    val colors = tone.colors()
    val resolvedIcon = icon ?: IconResource.Vector(tone.icon())

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(EchoTheme.shapes.card)
            .background(colors.container)
            .padding(
                horizontal = EchoTheme.spacing.padding.medium,
                vertical = EchoTheme.spacing.padding.small + 2.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.small),
        verticalAlignment = Alignment.Top,
    ) {
        resolvedIcon.Paint(
            modifier = Modifier
                .size(EchoTheme.dimen.icon.medium)
                .padding(top = 1.dp),
            color = colors.accent,
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = EchoTheme.typography.titleSmall,
                color = colors.content,
                fontWeight = FontWeight.SemiBold,
            )
            if (description != null) {
                Text(
                    text = description,
                    style = EchoTheme.typography.bodySmall,
                    color = colors.muted,
                )
            }
        }
    }
}
