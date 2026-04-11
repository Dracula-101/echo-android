package com.application.echo.features.messaging.di

import android.content.Context
import androidx.room.Room
import com.application.echo.api.message.MessageApiRepository
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.messaging.db.MessagingDatabase
import com.application.echo.features.messaging.db.dao.ConversationDao
import com.application.echo.features.messaging.db.dao.MessageDao
import com.application.echo.features.messaging.db.dao.ParticipantDao
import com.application.echo.features.messaging.db.dao.PendingMessageDao
import com.application.echo.features.messaging.db.dao.SyncMetadataDao
import com.application.echo.features.messaging.outbox.MessageOutbox
import com.application.echo.features.messaging.outbox.MessageOutboxImpl
import com.application.echo.features.messaging.repository.MessagingRepository
import com.application.echo.features.messaging.repository.MessagingRepositoryImpl
import com.application.echo.features.messaging.store.TransientMessagingState
import com.application.echo.features.messaging.sync.SyncEngine
import com.application.echo.features.messaging.sync.SyncEngineImpl
import com.application.echo.features.websocket.EchoWebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MessagingModule {

    @Provides
    @Singleton
    fun provideMessagingDatabase(
        @ApplicationContext context: Context,
    ): MessagingDatabase = Room.databaseBuilder(
        context,
        MessagingDatabase::class.java,
        "echo_messaging.db",
    ).fallbackToDestructiveMigration(true).build()

    @Provides
    fun provideConversationDao(db: MessagingDatabase): ConversationDao = db.conversationDao()

    @Provides
    fun provideParticipantDao(db: MessagingDatabase): ParticipantDao = db.participantDao()

    @Provides
    fun provideMessageDao(db: MessagingDatabase): MessageDao = db.messageDao()

    @Provides
    fun providePendingMessageDao(db: MessagingDatabase): PendingMessageDao = db.pendingMessageDao()

    @Provides
    fun provideSyncMetadataDao(db: MessagingDatabase): SyncMetadataDao = db.syncMetadataDao()

    @Provides
    @Singleton
    fun provideMessageOutbox(
        pendingMessageDao: PendingMessageDao,
        messageDao: MessageDao,
        conversationDao: ConversationDao,
        messageApiRepository: MessageApiRepository,
        @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): MessageOutbox = MessageOutboxImpl(
        pendingMessageDao = pendingMessageDao,
        messageDao = messageDao,
        conversationDao = conversationDao,
        messageApiRepository = messageApiRepository,
        ioDispatcher = ioDispatcher,
    )

    @Provides
    @Singleton
    fun provideSyncEngine(
        messageApiRepository: MessageApiRepository,
        conversationDao: ConversationDao,
        participantDao: ParticipantDao,
        messageDao: MessageDao,
        syncMetadataDao: SyncMetadataDao,
        authRepository: AuthRepository,
        @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): SyncEngine = SyncEngineImpl(
        messageApiRepository = messageApiRepository,
        conversationDao = conversationDao,
        participantDao = participantDao,
        messageDao = messageDao,
        syncMetadataDao = syncMetadataDao,
        authRepository = authRepository,
        ioDispatcher = ioDispatcher,
    )

    @Provides
    @Singleton
    fun provideMessagingRepository(
        conversationDao: ConversationDao,
        participantDao: ParticipantDao,
        messageDao: MessageDao,
        messageApiRepository: MessageApiRepository,
        webSocketManager: EchoWebSocketManager,
        authRepository: AuthRepository,
        transientState: TransientMessagingState,
        outbox: MessageOutbox,
        syncEngine: SyncEngine,
        @AppDispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): MessagingRepository = MessagingRepositoryImpl(
        conversationDao = conversationDao,
        participantDao = participantDao,
        messageDao = messageDao,
        messageApiRepository = messageApiRepository,
        webSocketManager = webSocketManager,
        authRepository = authRepository,
        transientState = transientState,
        outbox = outbox,
        syncEngine = syncEngine,
        ioDispatcher = ioDispatcher,
    )
}
