package com.application.echo.ui.components.pager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.application.echo.ui.design.theme.EchoTheme

@Composable
fun EchoLinePagerIndicator(
    currentPage: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    currentPageOffsetFraction: Float = 0f,
) {
    val primary = EchoTheme.colorScheme.primary.color
    val inactive = EchoTheme.colorScheme.surface.onColor.copy(alpha = 0.2f)

    // Single continuous value: e.g. page=1, offset=0.3 → 1.3
    val scrollPosition = currentPage + currentPageOffsetFraction

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(totalSteps) { index ->
            val progress by remember(scrollPosition) {
                derivedStateOf {
                    (scrollPosition - index + 1).coerceIn(0f, 1f)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(color = inactive, shape = RoundedCornerShape(50)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(color = primary, shape = RoundedCornerShape(50)),
                )
            }
        }
    }
}