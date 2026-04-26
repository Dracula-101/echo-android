package com.application.echo.presentation.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeMute
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.VolumeMute
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.application.echo.features.messaging.model.OnlineStatus
import com.application.echo.ui.design.theme.EchoTheme
import com.application.echo.ui.design.utils.alpha50
import com.application.echo.ui.design.utils.alpha60

@Composable
internal fun ConversationListItem(
    conversation: ConversationItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = EchoTheme.spacing.padding.small, vertical = EchoTheme.spacing.padding.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ConversationAvatar(
            avatarUrl = conversation.participantAvatarUrl,
            name = conversation.participantName,
            onlineStatus = conversation.onlineStatus,
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = conversation.participantName,
                    style = EchoTheme.typography.titleSmall,
                    color = EchoTheme.colorScheme.surface.onColor,
                    maxLines = 1,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else EchoTheme.typography.bodyMedium.fontWeight,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (conversation.conversationType == "GROUP") {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = EchoTheme.colorScheme.primary.color,
                        modifier = Modifier.size(12.dp),
                    )
                }
                if (conversation.conversationType == "MUTED") {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.VolumeMute,
                        contentDescription = null,
                        tint = EchoTheme.colorScheme.surface.onColor.alpha50,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(EchoTheme.spacing.padding.extraSmall))

            val isTyping = conversation.lastMessageContent == "typing..."
            Text(
                text = when {
                    isTyping -> "typing..."
                    conversation.isLastMessageFromMe -> "You: ${conversation.lastMessageContent}"
                    else -> conversation.lastMessageContent.orEmpty()
                },
                style = if (isTyping)
                    EchoTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic)
                else
                    EchoTheme.typography.bodySmall,
                color = EchoTheme.colorScheme.surface.onColor.alpha60,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = conversation.formattedTimestamp.orEmpty(),
                style = EchoTheme.typography.labelSmall,
                color = if (conversation.unreadCount > 0)
                    EchoTheme.colorScheme.surface.onColor
                else
                    EchoTheme.colorScheme.surface.onColor.alpha50,
            )
            if (conversation.unreadCount > 0) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                        .clip(CircleShape)
                        .background(EchoTheme.colorScheme.primary.color)
                        .padding(horizontal = 5.dp),
                ) {
                    Text(
                        text = conversation.unreadCount.toString(),
                        style = EchoTheme.typography.labelSmall,
                        color = EchoTheme.colorScheme.primary.onColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationAvatar(
    avatarUrl: String?,
    name: String,
    onlineStatus: OnlineStatus,
) {
    Box(modifier = Modifier.size(52.dp)) {
        if (avatarUrl != null) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(EchoTheme.colorScheme.primary.container),
            ) {
                Text(
                    text = name.first().uppercaseChar().toString(),
                    style = EchoTheme.typography.titleMedium,
                    color = EchoTheme.colorScheme.primary.color,
                )
            }
        }

        if (onlineStatus == OnlineStatus.ONLINE) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(EchoTheme.colorScheme.surface.color)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4ADE80))
                    .align(Alignment.BottomEnd),
            )
        }
    }
}

private object AvatarUrls {
    const val AMAYA  = "https://i.pravatar.cc/150?img=47"
    const val DEV    = "https://i.pravatar.cc/150?img=11"
    const val LIORA  = "https://i.pravatar.cc/150?img=32"
    const val MARCO  = "https://i.pravatar.cc/150?img=15"
    const val SOFIA  = "https://i.pravatar.cc/150?img=56"
    const val JORDAN = "https://i.pravatar.cc/150?img=3"
    const val NOUR   = "https://i.pravatar.cc/150?img=45"
    const val PRIYA  = "https://i.pravatar.cc/150?img=38"
    const val ETHAN  = "https://i.pravatar.cc/150?img=7"
    const val ZARA   = "https://i.pravatar.cc/150?img=49"
    const val LUCA   = "https://i.pravatar.cc/150?img=14"
}

internal val sampleConversations = listOf(
    ConversationItem(
        conversationId = "1",
        participantName = "Amaya Okafor",
        participantAvatarUrl = AvatarUrls.AMAYA,
        onlineStatus = OnlineStatus.ONLINE,
        lastMessageContent = "Just landed — the light here is unreal 🌅",
        formattedTimestamp = "2m",
        isLastMessageFromMe = false,
        conversationType = null,
        unreadCount = 2,
    ),
    ConversationItem(
        conversationId = "2",
        participantName = "Kite Club",
        participantAvatarUrl = null, // group — no avatar
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "typing...",
        formattedTimestamp = "8m",
        isLastMessageFromMe = false,
        conversationType = "GROUP",
        unreadCount = 5,
    ),
    ConversationItem(
        conversationId = "3",
        participantName = "Dev Patel",
        participantAvatarUrl = AvatarUrls.DEV,
        onlineStatus = OnlineStatus.ONLINE,
        lastMessageContent = "sent the Figma link",
        formattedTimestamp = "24m",
        isLastMessageFromMe = true,
        conversationType = null,
        unreadCount = 0,
    ),
    ConversationItem(
        conversationId = "4",
        participantName = "Mom",
        participantAvatarUrl = null, // fallback initial
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "Call me when you can, love",
        formattedTimestamp = "1h",
        isLastMessageFromMe = false,
        conversationType = null,
        unreadCount = 1,
    ),
    ConversationItem(
        conversationId = "5",
        participantName = "Studio Sol",
        participantAvatarUrl = null, // group — no avatar
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "Priya: new moodboard is up",
        formattedTimestamp = "3h",
        isLastMessageFromMe = false,
        conversationType = "MUTED",
        unreadCount = 0,
    ),
    ConversationItem(
        conversationId = "6",
        participantName = "Liora Ben",
        participantAvatarUrl = AvatarUrls.LIORA,
        onlineStatus = OnlineStatus.ONLINE,
        lastMessageContent = "are you coming tonight?",
        formattedTimestamp = "3h",
        isLastMessageFromMe = false,
        conversationType = null,
        unreadCount = 3,
    ),
    ConversationItem(
        conversationId = "7",
        participantName = "Marco Rivera",
        participantAvatarUrl = AvatarUrls.MARCO,
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "the PR is ready for review",
        formattedTimestamp = "5h",
        isLastMessageFromMe = false,
        conversationType = null,
        unreadCount = 0,
    ),
    ConversationItem(
        conversationId = "8",
        participantName = "Design Team",
        participantAvatarUrl = null, // group — no avatar
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "typing...",
        formattedTimestamp = "6h",
        isLastMessageFromMe = false,
        conversationType = "GROUP",
        unreadCount = 12,
    ),
    ConversationItem(
        conversationId = "9",
        participantName = "Sofia Reyes",
        participantAvatarUrl = AvatarUrls.SOFIA,
        onlineStatus = OnlineStatus.ONLINE,
        lastMessageContent = "sent you a voice message",
        formattedTimestamp = "7h",
        isLastMessageFromMe = false,
        conversationType = null,
        unreadCount = 1,
    ),
    ConversationItem(
        conversationId = "10",
        participantName = "Jordan Kim",
        participantAvatarUrl = AvatarUrls.JORDAN,
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "ok sounds good 👍",
        formattedTimestamp = "9h",
        isLastMessageFromMe = false,
        conversationType = null,
        unreadCount = 0,
    ),
    ConversationItem(
        conversationId = "11",
        participantName = "Nour Khalil",
        participantAvatarUrl = AvatarUrls.NOUR,
        onlineStatus = OnlineStatus.ONLINE,
        lastMessageContent = "I'll send the invoice tomorrow",
        formattedTimestamp = "10h",
        isLastMessageFromMe = false,
        conversationType = null,
        unreadCount = 0,
    ),
    ConversationItem(
        conversationId = "12",
        participantName = "Weekend Crew",
        participantAvatarUrl = null, // group — no avatar
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "Jake: I'm already there lol",
        formattedTimestamp = "11h",
        isLastMessageFromMe = false,
        conversationType = "GROUP",
        unreadCount = 7,
    ),
    ConversationItem(
        conversationId = "13",
        participantName = "Priya Nair",
        participantAvatarUrl = AvatarUrls.PRIYA,
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "check your email",
        formattedTimestamp = "Yesterday",
        isLastMessageFromMe = false,
        conversationType = "MUTED",
        unreadCount = 0,
    ),
    ConversationItem(
        conversationId = "14",
        participantName = "Ethan Cole",
        participantAvatarUrl = AvatarUrls.ETHAN,
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "sure, let's do Tuesday",
        formattedTimestamp = "Yesterday",
        isLastMessageFromMe = true,
        conversationType = null,
        unreadCount = 0,
    ),
    ConversationItem(
        conversationId = "15",
        participantName = "Zara Ahmed",
        participantAvatarUrl = AvatarUrls.ZARA,
        onlineStatus = OnlineStatus.ONLINE,
        lastMessageContent = "omg did you see that??",
        formattedTimestamp = "Yesterday",
        isLastMessageFromMe = false,
        conversationType = null,
        unreadCount = 4,
    ),
    ConversationItem(
        conversationId = "16",
        participantName = "Book Club",
        participantAvatarUrl = null, // group — no avatar
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "Mia: chapter 12 destroyed me",
        formattedTimestamp = "Mon",
        isLastMessageFromMe = false,
        conversationType = "GROUP",
        unreadCount = 0,
    ),
    ConversationItem(
        conversationId = "17",
        participantName = "Luca Ferrari",
        participantAvatarUrl = AvatarUrls.LUCA,
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "sent the updated specs",
        formattedTimestamp = "Mon",
        isLastMessageFromMe = true,
        conversationType = null,
        unreadCount = 0,
    ),
    ConversationItem(
        conversationId = "18",
        participantName = "Dad",
        participantAvatarUrl = null, // fallback initial
        onlineStatus = OnlineStatus.UNKNOWN,
        lastMessageContent = "Drive safe ❤️",
        formattedTimestamp = "Sun",
        isLastMessageFromMe = false,
        conversationType = null,
        unreadCount = 0,
    ),
)