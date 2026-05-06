package com.application.echo.presentation.create_profile

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.echo.ui.components.avatar.EchoAvatar
import com.application.echo.ui.components.card.EchoCard
import com.application.echo.ui.components.common.EchoPill
import com.application.echo.ui.components.progress.EchoCircularProgressIndicator
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha90

@Composable
internal fun HandleInfo(
    state: CreateProfileState,
    listState: LazyListState,
    onAction: (CreateProfileAction) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                buildAnnotatedString {
                    append("Pick a ")
                    withStyle(
                        EchoTheme.typography.headlineMedium
                            .toSpanStyle()
                            .copy(
                                color = EchoTheme.colorScheme.primary.color,
                                fontWeight = FontWeight.ExtraBold
                            )
                    ) {
                        append("handle")
                    }
                },
                style = EchoTheme.typography.headlineMedium,
                color = EchoTheme.colorScheme.surface.onColor,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.small))
            Text(
                "This is how friends find you on Echo. Letters, numbers, underscores.",
                style = EchoTheme.typography.bodyMedium,
                color = EchoTheme.colorScheme.surface.onColor.alpha50,
            )
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
        }
        item{
            EchoCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.large)
                ) {
                    Box(
                        modifier = Modifier
                            .size(EchoTheme.dimen.component.large)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = avatarGradientStops[state.selectedGradientIndex].toList(),
                                    start = Offset(0f, 0f),
                                    end = Offset.Infinite
                                ),
                                shape = CircleShape
                            )
                    ) {
                        Text(
                            text = profileEmojis[state.selectedEmojiIndex],
                            style = EchoTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Column {
                        Text(
                            text = state.displayName ?: "",
                            style = EchoTheme.typography.headlineSmall,
                        )
                        Text(
                            buildAnnotatedString {
                                append("@")
                                if (state.userName != null) {
                                    withStyle(
                                        EchoTheme.typography.bodyMedium
                                            .toSpanStyle()
                                            .copy(
                                                color = EchoTheme.colorScheme.primary.color,
                                                fontWeight = FontWeight.Medium
                                            )
                                    ) {
                                        append(state.userName)
                                    }
                                } else {
                                    withStyle(
                                        EchoTheme.typography.bodyMedium
                                            .toSpanStyle()
                                            .copy(
                                                color = EchoTheme.colorScheme.surface.onColor.alpha50,
                                                fontWeight = FontWeight.Medium
                                            )
                                    ) {
                                        append("username")
                                    }
                                }
                            },
                            style = EchoTheme.typography.bodyMedium,
                            color = EchoTheme.colorScheme.primary.color,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
        }
        item {
            EchoTextField(
                value = state.userName ?: "",
                onValueChange = {
                    onAction(CreateProfileAction.OnUserNameChanged(it))
                },
                label = {
                    Text(
                        "USERNAME",
                        style = EchoTheme.typography.labelSmall,
                        letterSpacing = 1.25.sp,
                        fontWeight = FontWeight.Medium,
                        color = EchoTheme.colorScheme.surface.onColor.alpha90,
                    )
                },
                placeholder = "Choose a unique username",
                errorText = state.userNameError,
                isError = state.userNameError != null,
                borderColor = if (state.isValidUserName) {
                    EchoTheme.colorScheme.success.color
                } else null,
                trailing = {
                    when {
                        state.isValidatingUserName -> {
                            EchoCircularProgressIndicator(
                                modifier = Modifier.size(EchoTheme.dimen.icon.small),
                                color = EchoTheme.colorScheme.primary.color,
                                strokeWidth = 2.dp
                            )
                        }
                        state.isValidUserName -> {
                            Box(
                                modifier = Modifier
                                    .background(
                                        EchoTheme.colorScheme.success.color,
                                        shape = RoundedCornerShape(50)
                                    ).padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = EchoTheme.colorScheme.success.onColor,
                                    modifier = Modifier.size(EchoTheme.dimen.icon.small)
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}