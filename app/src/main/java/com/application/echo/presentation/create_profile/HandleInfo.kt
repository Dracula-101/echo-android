package com.application.echo.presentation.create_profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.application.echo.ui.components.switch.EchoSwitch
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha10
import com.application.echo.ui.design.utils.alpha20
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha90

@Composable
internal fun HandleInfo(
    state: CreateProfileState,
    onAction: (CreateProfileAction) -> Unit,
) {
    val listState = rememberLazyListState()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            onAction(CreateProfileAction.OnNotificationChanged(isGranted))
        }
    )
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
                                    )
                                    .padding(2.dp),
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
            if (state.isValidUserName) {
                Text(
                    "✓ @${state.userName} is yours!",
                    style = EchoTheme.typography.labelSmall,
                    color = EchoTheme.colorScheme.success.color,
                    modifier = Modifier.padding(top = EchoTheme.spacing.gap.small)
                )
            }
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
        }
        item {
            Text(
                "PREFERENCES",
                style = EchoTheme.typography.labelSmall,
                letterSpacing = 1.25.sp,
                fontWeight = FontWeight.Medium,
                color = EchoTheme.colorScheme.surface.onColor.alpha90,
            )
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.medium))
            EchoCard (
                modifier = Modifier.fillMaxWidth()
            ){
                Row(
                    horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EchoTheme.colorScheme.secondary.color.alpha20, CircleShape)
                            .padding(8.dp)
                            .size(EchoTheme.dimen.icon.small),
                        tint = EchoTheme.colorScheme.secondary.color
                    )
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Let friends find me by handle",
                            style = EchoTheme.typography.bodyMedium,
                        )
                        Text(
                            "You can change this in Settings",
                            style = EchoTheme.typography.labelSmall,
                            color = EchoTheme.colorScheme.surface.onColor.alpha50,
                        )
                    }
                    EchoSwitch(
                        checked = { state.isSearchable },
                        onCheckedChange = {
                            onAction(CreateProfileAction.OnToggleSearchable)
                         },
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = EchoTheme.spacing.gap.medium),
                    color = EchoTheme.colorScheme.surface.onColor.alpha10
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.gap.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EchoTheme.colorScheme.success.color.alpha20, CircleShape)
                            .padding(8.dp)
                            .size(EchoTheme.dimen.icon.small),
                        tint = EchoTheme.colorScheme.success.color
                    )
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Allow notifications",
                            style = EchoTheme.typography.bodyMedium,
                        )
                        Text(
                            "Enable to stay updated with mentions and messages",
                            style = EchoTheme.typography.labelSmall,
                            color = EchoTheme.colorScheme.surface.onColor.alpha50,
                        )
                    }
                    EchoSwitch(
                        checked = { state.allowNotifications },
                        onCheckedChange = {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        },
                        enabled = !state.allowNotifications
                    )
                }
            }
        }
    }
}