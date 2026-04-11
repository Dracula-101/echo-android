package com.application.echo.features.messaging.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.application.echo.features.messaging.db.dao.ConversationDao
import com.application.echo.features.messaging.db.dao.MessageDao
import com.application.echo.features.messaging.db.dao.ParticipantDao
import com.application.echo.features.messaging.db.dao.PendingMessageDao
import com.application.echo.features.messaging.db.dao.SyncMetadataDao
import com.application.echo.features.messaging.db.entity.ConversationEntity
import com.application.echo.features.messaging.db.entity.MessageEntity
import com.application.echo.features.messaging.db.entity.ParticipantEntity
import com.application.echo.features.messaging.db.entity.PendingMessageEntity
import com.application.echo.features.messaging.db.entity.SyncMetadataEntity

@Database(
    entities = [
        ConversationEntity::class,
        ParticipantEntity::class,
        MessageEntity::class,
        PendingMessageEntity::class,
        SyncMetadataEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class MessagingDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun participantDao(): ParticipantDao
    abstract fun messageDao(): MessageDao
    abstract fun pendingMessageDao(): PendingMessageDao
    abstract fun syncMetadataDao(): SyncMetadataDao
}
