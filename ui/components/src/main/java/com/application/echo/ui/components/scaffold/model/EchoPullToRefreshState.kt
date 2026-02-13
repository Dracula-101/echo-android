package com.application.echo.ui.components.scaffold.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * The state of the pull-to-refresh.
 */
data class EchoPullToRefreshState(
    val isEnabled: Boolean,
    val isRefreshing: Boolean,
    val onRefresh: () -> Unit,
)

/**
 * Create and remember the default [EchoPullToRefreshState].
 */
@Composable
fun rememberEchoPullToRefreshState(
    isEnabled: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = { },
): EchoPullToRefreshState = remember(isEnabled, isRefreshing, onRefresh) {
    EchoPullToRefreshState(
        isEnabled = isEnabled,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    )
}
