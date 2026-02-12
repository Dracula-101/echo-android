package com.application.echo.ui.components.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.application.echo.ui.design.theme.EchoTheme

/**
 * Layout style for [EchoTopBar].
 */
enum class EchoTopBarStyle {
    /** Start-aligned title (standard top bar). */
    Small,

    /** Center-aligned title. */
    CenterAligned,
}

/**
 * Themed top app bar with title, optional navigation icon, and action buttons.
 *
 * ```kotlin
 * EchoTopBar(
 *     title = "Messages",
 *     navigationIcon = {
 *         EchoIconButton(icon = IconResource.Vector(Icons.Default.ArrowBack), onClick = ::navigateUp)
 *     },
 *     actions = {
 *         EchoIconButton(icon = IconResource.Vector(Icons.Default.Search), onClick = ::search)
 *     },
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoTopBar(
    title: String,
    modifier: Modifier = Modifier,
    style: EchoTopBarStyle = EchoTopBarStyle.Small,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    EchoTopBar(
        title = {
            Text(
                text = title,
                style = EchoTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier,
        style = style,
        navigationIcon = navigationIcon,
        actions = actions,
        scrollBehavior = scrollBehavior,
    )
}

/**
 * Themed top app bar with a composable title slot.
 *
 * Use the [String] overload when you only need a simple text title.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoTopBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: EchoTopBarStyle = EchoTopBarStyle.Small,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = EchoTheme.colorScheme.background.color,
        scrolledContainerColor = EchoTheme.colorScheme.surface.color,
        navigationIconContentColor = EchoTheme.colorScheme.surface.onColor,
        titleContentColor = EchoTheme.colorScheme.surface.onColor,
        actionIconContentColor = EchoTheme.colorScheme.surface.onColor,
    )

    when (style) {
        EchoTopBarStyle.Small -> {
            TopAppBar(
                title = title,
                modifier = modifier,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior,
            )
        }

        EchoTopBarStyle.CenterAligned -> {
            CenterAlignedTopAppBar(
                title = title,
                modifier = modifier,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior,
            )
        }
    }
}
