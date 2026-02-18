package com.application.echo.ui.components.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.components.navigation.model.NavigationItem
import com.application.echoplatform.components.navigation.EchoNavigationBarItem
import kotlinx.collections.immutable.ImmutableList

/**
 * A custom Echo-themed bottom app bar.
 */
@Composable
fun EchoBottomAppBar(
    navigationItems: ImmutableList<NavigationItem>,
    selectedItem: NavigationItem?,
    onClick: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
) {
    BottomAppBar(
        containerColor = EchoTheme.colorScheme.secondary.container,
        contentColor = Color.Unspecified,
        windowInsets = windowInsets,
        modifier = modifier,
    ) {
        navigationItems.forEach { navigationItem ->
            EchoNavigationBarItem(
                labelRes = navigationItem.labelRes,
                contentDescriptionRes = navigationItem.contentDescriptionRes,
                selectedIconRes = navigationItem.iconResSelected,
                unselectedIconRes = navigationItem.iconRes,
                notificationCount = navigationItem.notificationCount,
                isSelected = selectedItem == navigationItem,
                onClick = { onClick(navigationItem) },
                modifier = Modifier.testTag(tag = navigationItem.testTag),
            )
        }
    }
}
