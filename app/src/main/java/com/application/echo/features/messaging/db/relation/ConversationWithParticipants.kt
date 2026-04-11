package com.application.echo.features.messaging.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.application.echo.features.messaging.db.entity.ConversationEntity
import com.application.echo.features.messaging.db.entity.ParticipantEntity

data class ConversationWithParticipants(
    @Embedded val conversation: ConversationEntity,
    @Relation(parentColumn = "id", entityColumn = "conversationId")
    val participants: List<ParticipantEntity>,
)
