package com.application.echo.features.messaging.model

import com.application.echo.api.message.ConversationResponse
import com.application.echo.api.message.MessageResponse
import com.application.echo.features.messaging.db.entity.ConversationEntity
import com.application.echo.features.messaging.db.entity.MessageEntity
import com.application.echo.features.messaging.db.entity.ParticipantEntity
import com.application.echo.features.messaging.db.relation.ConversationWithParticipants

// ── API → DB ────────────────────────────────────────────────────────

fun ConversationResponse.toEntity(): ConversationEntity = ConversationEntity(
    id = id,
    conversationType = conversationType.orEmpty(),
    memberCount = memberCount ?: 0,
    messageCount = messageCount ?: 0,
    unreadCount = unreadCount ?: 0,
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    lastMessageId = lastMessage?.id,
    lastMessageContent = lastMessage?.content,
    lastMessageSenderId = lastMessage?.senderId,
    lastMessageTimestamp = lastMessage?.createdAt,
    lastMessageStatus = lastMessage?.status,
)

fun ConversationResponse.toParticipantEntities(): List<ParticipantEntity> =
    participants?.map { participant ->
        ParticipantEntity(
            conversationId = id,
            userId = participant.userId,
            displayName = participant.displayName.orEmpty(),
            avatarUrl = (participant.userAvatar ?: participant.avatarUrl).orEmpty(),
            userName = participant.userName.orEmpty(),
            onlineStatus = participant.onlineStatus.orEmpty(),
        )
    } ?: emptyList()

fun MessageResponse.toEntity(currentUserId: String): MessageEntity = MessageEntity(
    id = id,
    conversationId = conversationId,
    senderId = senderId,
    content = content.orEmpty(),
    messageType = messageType.orEmpty(),
    status = status.orEmpty(),
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
    isOutgoing = senderId == currentUserId,
    localId = null,
)

// ── DB → Domain ─────────────────────────────────────────────────────

fun ConversationWithParticipants.toDomain(): Conversation = Conversation(
    id = conversation.id,
    conversationType = conversation.conversationType,
    participants = participants.map { it.toDomain() },
    lastMessage = conversation.lastMessageId?.let {
        LastMessage(
            id = it,
            content = conversation.lastMessageContent,
            senderId = conversation.lastMessageSenderId.orEmpty(),
            timestamp = conversation.lastMessageTimestamp.orEmpty(),
            status = conversation.lastMessageStatus,
        )
    },
    memberCount = conversation.memberCount,
    messageCount = conversation.messageCount,
    unreadCount = conversation.unreadCount,
    createdAt = conversation.createdAt,
    updatedAt = conversation.updatedAt,
)

fun ParticipantEntity.toDomain(): Participant = Participant(
    userId = userId,
    displayName = displayName,
    avatarUrl = avatarUrl,
    userName = userName,
    onlineStatus = OnlineStatus.from(onlineStatus),
)

fun MessageEntity.toDomain(): Message = Message(
    id = id,
    conversationId = conversationId,
    senderId = senderId,
    content = content,
    messageType = messageType,
    status = MessageDeliveryStatus.from(status),
    createdAt = createdAt,
    updatedAt = updatedAt,
    isOutgoing = isOutgoing,
    localId = localId,
)
