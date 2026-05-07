package com.application.echo.presentation.create_profile

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.application.echo.ui.components.common.EchoPill
import com.application.echo.ui.components.textfield.EchoTextField
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha90

@Composable
internal fun AvatarInfo(
    state: CreateProfileState,
    onAction: (CreateProfileAction) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(state.bioError) {
        if (state.bioError != null) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                buildAnnotatedString {
                    append("Who are ")
                    withStyle(
                        EchoTheme.typography.headlineMedium
                            .toSpanStyle()
                            .copy(
                                color = EchoTheme.colorScheme.primary.color,
                                fontWeight = FontWeight.ExtraBold
                            )
                    ) {
                        append("you")
                    }
                    append("?")
                },
                style = EchoTheme.typography.headlineMedium,
                color = EchoTheme.colorScheme.surface.onColor,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.small))
            Text(
                "Add a name and a look. You can change either anytime.",
                style = EchoTheme.typography.bodyMedium,
                color = EchoTheme.colorScheme.surface.onColor.alpha50,
            )
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ){
                Box(
                    modifier = Modifier
                        .height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = avatarGradientStops[state.selectedGradientIndex].toList(),
                                    start = Offset(0f, 0f),
                                    end = Offset.Infinite
                                ),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = profileEmojis[state.selectedEmojiIndex],
                            fontSize = 48.sp,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset((-4).dp, (-4).dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EchoTheme.colorScheme.background.color)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(EchoTheme.colorScheme.primary.color),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(16.dp),
                            colorFilter = ColorFilter.tint(EchoTheme.colorScheme.primary.onColor)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
        }
        item {
            EchoTextField(
                value = state.displayName ?: "",
                onValueChange = {
                    onAction(CreateProfileAction.OnDisplayNameChanged(it))
                },
                errorText = state.displayNameError,
                isError = state.displayNameError != null,
                label = {
                    Text(
                        "NAME",
                        style = EchoTheme.typography.labelSmall,
                        letterSpacing = 1.25.sp,
                        fontWeight = FontWeight.Medium,
                        color = EchoTheme.colorScheme.surface.onColor.alpha90,
                    )
                },
            )
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraSmall))
            Text(
                "This is how you'll show up in chats.",
                style = EchoTheme.typography.labelSmall,
                color = EchoTheme.colorScheme.surface.onColor.alpha50,
            )
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.large))
        }
        item {
            Row {
                Text(
                    "GENDER",
                    style = EchoTheme.typography.labelSmall,
                    letterSpacing = 1.25.sp,
                    fontWeight = FontWeight.Medium,
                    color = EchoTheme.colorScheme.surface.onColor.alpha90,
                )
                Text(
                    "· optional",
                    style = EchoTheme.typography.labelSmall,
                    letterSpacing = 1.25.sp,
                    fontWeight = FontWeight.Medium,
                    color = EchoTheme.colorScheme.surface.onColor.alpha50,
                )
            }
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.large))
        }
        item {
            Text(
                "PICK A LOOK",
                style = EchoTheme.typography.labelSmall,
                letterSpacing = 1.25.sp,
                fontWeight = FontWeight.Medium,
                color = EchoTheme.colorScheme.surface.onColor.alpha90,
            )
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.small))
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                avatarGradientStops.forEachIndexed { index, currentGradient ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(EchoTheme.shapes.cardSmall)
                            .background(
                                Brush.linearGradient(
                                    colors = currentGradient.toList(),
                                    start = Offset(0f, 0f),
                                    end = Offset.Infinite
                                )
                            )
                            .then(
                                if (state.selectedGradientIndex == index) {
                                    Modifier.border(
                                        width = 2.dp,
                                        color = EchoTheme.colorScheme.surface.onColor,
                                        shape = EchoTheme.shapes.cardSmall
                                    )
                                } else {
                                    Modifier.border(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(
                                            colors = currentGradient.toList(),
                                            start = Offset.Infinite,
                                            end = Offset(0f, 0f)
                                        ),
                                        shape = RoundedCornerShape(0.dp)
                                    )
                                }
                            )
                            .clickable {
                                onAction(CreateProfileAction.OnGradientSelected(index))
                            },
                    )
                }
            }
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.small))
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                profileEmojis.forEachIndexed { index, emoji ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(EchoTheme.shapes.cardSmall)
                            .background(
                                EchoTheme.colorScheme.surface.highest
                            )
                            .then(
                                if (state.selectedEmojiIndex == index) {
                                    Modifier.border(
                                        width = 2.dp,
                                        color = EchoTheme.colorScheme.surface.onColor,
                                        shape = EchoTheme.shapes.cardSmall
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .clickable {
                                onAction(CreateProfileAction.OnEmojiSelected(index))
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 24.sp,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
        }
        item {
            EchoTextField(
                value = state.bio ?: "",
                onValueChange = {
                    onAction(CreateProfileAction.OnBioChanged(it))
                },
                label = {
                    Text(
                        "BIO",
                        style = EchoTheme.typography.labelSmall,
                        letterSpacing = 1.25.sp,
                        fontWeight = FontWeight.Medium,
                        color = EchoTheme.colorScheme.surface.onColor.alpha90,
                    )
                },
                errorText = state.bioError,
                isError = state.bioError != null,
                labelTrailing = {
                    Text(
                        "${state.bio?.length ?: 0}/150",
                        style = EchoTheme.typography.labelSmall,
                        color = EchoTheme.colorScheme.surface.onColor.alpha50,
                    )
                },
                minLines = 3,
                singleLine = false,
            )
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
        }
        item {
            Spacer(modifier = Modifier.height(EchoTheme.spacing.gap.extraLarge))
        }
    }
}

sealed interface AvatarPictureInfo {
    data class Uri(val uri: android.net.Uri) : AvatarPictureInfo
    data class Custom(
        val gradients: List<Color>,
        val emoji: String,
    )
}