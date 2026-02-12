package com.application.echo.ui.components.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Elevation style for [EchoCard].
 */
enum class EchoCardStyle {
    /** Flat card with a subtle border — default style. */
    Outlined,

    /** Slightly elevated card with a shadow. */
    Elevated,

    /** Filled card with a tinted background, no border. */
    Filled,
}

/**
 * Themed card container that adapts to [EchoCardStyle].
 *
 * ```kotlin
 * EchoCard {
 *     Text("Content inside a card")
 * }
 *
 * EchoCard(
 *     style = EchoCardStyle.Elevated,
 *     onClick = ::navigateToDetail,
 * ) {
 *     Text("Clickable elevated card")
 * }
 * ```
 */
@Composable
fun EchoCard(
    modifier: Modifier = Modifier,
    style: EchoCardStyle = EchoCardStyle.Outlined,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = EchoTheme.shapes.card
    val colorScheme = EchoTheme.colorScheme

    val colors = when (style) {
        EchoCardStyle.Outlined -> CardDefaults.cardColors(
            containerColor = colorScheme.surface.container,
            contentColor = colorScheme.surface.onContainer,
        )
        EchoCardStyle.Elevated -> CardDefaults.elevatedCardColors(
            containerColor = colorScheme.surface.container,
            contentColor = colorScheme.surface.onContainer,
        )
        EchoCardStyle.Filled -> CardDefaults.cardColors(
            containerColor = colorScheme.surface.variant,
            contentColor = colorScheme.surface.onColor,
        )
    }

    val elevation = when (style) {
        EchoCardStyle.Outlined -> CardDefaults.cardElevation()
        EchoCardStyle.Elevated -> CardDefaults.elevatedCardElevation(
            defaultElevation = EchoTheme.spacing.elevation.medium,
        )
        EchoCardStyle.Filled -> CardDefaults.cardElevation()
    }

    val border = when (style) {
        EchoCardStyle.Outlined -> BorderStroke(
            width = EchoTheme.dimen.border.small,
            color = colorScheme.outline.color.copy(alpha = 0.4f),
        )
        else -> null
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
        ) {
            Column(
                modifier = Modifier.padding(EchoTheme.spacing.padding.medium),
                content = content,
            )
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
        ) {
            Column(
                modifier = Modifier.padding(EchoTheme.spacing.padding.medium),
                content = content,
            )
        }
    }
}
