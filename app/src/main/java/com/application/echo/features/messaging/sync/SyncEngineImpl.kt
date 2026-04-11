package com.application.echo.features.messaging.sync

import com.application.echo.api.message.MessageApiRepository
import com.application.echo.core.common.annotations.AppDispatcher
import com.application.echo.core.common.model.AppDispatchers
import com.application.echo.core.network.result.fold
import com.application.echo.features.auth.repository.AuthRepository
import com.application.echo.features.messaging.db.dao.ConversationDao
import com.application.echo.features.messaging.db.dao.MessageDao
import com.application.echo.features.messaging.db.dao.ParticipantDao
import com.application.echo.features.messaging.db.dao.SyncMetadataDao
import com.application.echo.features.messaging.db.entity.SyncMetadataEntity
import com.application.echo.features.messaging.model.toEntity
import com.application.echo.features.messaging.model.toParticipantEntities
import com.application.echo.features.messaging.sync.SyncEngine.Companion.RESOURCE_CONVERSATIONS
import com.application.echo.features.messaging.sync.SyncEngine.Companion.resourceMessages
import com.application.echo.features.websocket.model.SyncConversation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SyncEngineImpl @Inject constructor(
    private val messageApiRepository: MessageApiRepository,
    private val conversationDao: ConversationDao,
    private val participantDao: ParticipantDao,
    private val messageDao: MessageDao,
    private val syncMetadataDao: SyncMetadataDao,
    private val authRepository: AuthRepository,
    @AppDispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SyncEngine {

    /** Prevents concurrent syncs of the same resource from racing. */
    private val syncLocks = SyncLockManager()

    private val currentUserId: String
        get() = authRepository.userStateFlow.value.userId

    // ── Conversations ───────────────────────────────────────────────────

    override suspend fun syncConversations(forceFullSync: Boolean) {
        syncLocks.withLock(RESOURCE_CONVERSATIONS) {
            withContext(ioDispatcher) {
                val metadata = if (forceFullSync) null else syncMetadataDao.get(RESOURCE_CONVERSATIONS)
                if (metadata == null || !metadata.initialSyncComplete) {
                    initialSyncConversations()
                } else {
                    incrementalSyncConversations(metadata)
                }
            }
        }
    }

    private suspend fun initialSyncConversations() {
        Timber.d("SyncEngine: Initial sync conversations")
        messageApiRepository.getMyConversations().fold(
            onSuccess = { responses ->
                val entities = responses.map { it.toEntity() }
                val participants = responses.flatMap { it.toParticipantEntities() }
                conversationDao.upsertAll(entities)
                participantDao.upsertAll(participants)

                val latestTimestamp = responses.maxOfOrNull { it.updatedAt.orEmpty() }

                syncMetadataDao.upsert(
                    SyncMetadataEntity(
                        resourceType = RESOURCE_CONVERSATIONS,
                        lastSyncedAt = latestTimestamp,
                        cursor = null,
                        initialSyncComplete = true,
                        failureCount = 0,
                        lastSuccessEpochMs = System.currentTimeMillis(),
                    )
                )
                Timber.d("SyncEngine: Initial sync complete — %d conversations", responses.size)
            },
            onFailure = { exception ->
                syncMetadataDao.incrementFailureCount(RESOURCE_CONVERSATIONS)
                Timber.e("SyncEngine: Initial sync conversations failed: %s", exception.throwable.message)
                throw exception.throwable
            },
        )
    }

    private suspend fun incrementalSyncConversations(metadata: SyncMetadataEntity) {
        Timber.d("SyncEngine: Incremental sync conversations since %s", metadata.lastSyncedAt)
        messageApiRepository.getMyConversations(
            updatedSince = metadata.lastSyncedAt,
        ).fold(
            onSuccess = { responses ->
                if (responses.isNotEmpty()) {
                    val entities = responses.map { it.toEntity() }
                    val participants = responses.flatMap { it.toParticipantEntities() }
                    conversationDao.upsertAll(entities)
                    participantDao.upsertAll(participants)
                }

                val latestTimestamp = responses.maxOfOrNull { it.updatedAt.orEmpty() }
                    ?: metadata.lastSyncedAt

                syncMetadataDao.upsert(
                    metadata.copy(
                        lastSyncedAt = latestTimestamp,
                        failureCount = 0,
                        lastSuccessEpochMs = System.currentTimeMillis(),
                    )
                )
                Timber.d("SyncEngine: Incremental sync complete — %d updated conversations", responses.size)
            },
            onFailure = { exception ->
                syncMetadataDao.incrementFailureCount(RESOURCE_CONVERSATIONS)
                Timber.e("SyncEngine: Incremental sync conversations failed: %s", exception.throwable.message)
                throw exception.throwable
            },
        )
    }

    // ── Messages ────────────────────────────────────────────────────────

    override suspend fun syncMessages(conversationId: String, forceFullSync: Boolean) {
        val resourceKey = resourceMessages(conversationId)
        syncLocks.withLock(resourceKey) {
            withContext(ioDispatcher) {
                val metadata = if (forceFullSync) null else syncMetadataDao.get(resourceKey)
                if (metadata == null || !metadata.initialSyncComplete || metadata.cursor == null) {
                    // No prior sync, incomplete sync, or no cursor (empty conversation) —
                    // do a full initial fetch via GET /messages.
                    initialSyncMessages(conversationId, resourceKey)
                } else {
                    // We have a valid message ID cursor — use GET /sync for delta catch-up.
                    incrementalSyncMessages(conversationId, resourceKey, metadata)
                }
            }
        }
    }

    /**
     * Initial message fetch — uses the regular GET /messages endpoint
     * to load the most recent messages for a conversation.
     */
    private suspend fun initialSyncMessages(conversationId: String, resourceKey: String) {
        Timber.d("SyncEngine: Initial sync messages for %s", conversationId)
        messageApiRepository.getMessages(
            conversationId = conversationId,
            limit = INITIAL_MESSAGE_FETCH_LIMIT,
        ).fold(
            onSuccess = { response ->
                val entities = response.messages.map { it.toEntity(currentUserId) }
                messageDao.upsertAll(entities)

                // Store the latest message ID as our sync cursor for future incremental syncs.
                // The /sync endpoint uses last_message_id, not timestamps.
                val latestMessageId = response.messages.maxByOrNull { it.createdAt.orEmpty() }?.id
                val latestTimestamp = response.messages.maxOfOrNull { it.createdAt.orEmpty() }

                syncMetadataDao.upsert(
                    SyncMetadataEntity(
                        resourceType = resourceKey,
                        lastSyncedAt = latestTimestamp,
                        cursor = latestMessageId,
                        initialSyncComplete = true,
                        failureCount = 0,
                        lastSuccessEpochMs = System.currentTimeMillis(),
                    )
                )
                Timber.d(
                    "SyncEngine: Initial sync messages complete — %d messages, hasMore=%b, cursor=%s",
                    response.messages.size, response.hasMore, latestMessageId,
                )
            },
            onFailure = { exception ->
                syncMetadataDao.incrementFailureCount(resourceKey)
                Timber.e("SyncEngine: Initial sync messages failed for %s: %s", conversationId, exception.throwable.message)
                throw exception.throwable
            },
        )
    }

    /**
     * Incremental message sync — uses the dedicated GET /sync endpoint
     * with the last_message_id cursor to catch up on missed messages.
     * Paginates until has_more is false.
     */
    private suspend fun incrementalSyncMessages(
        conversationId: String,
        resourceKey: String,
        metadata: SyncMetadataEntity,
    ) {
        Timber.d(
            "SyncEngine: Incremental sync messages for %s, cursor=%s",
            conversationId, metadata.cursor,
        )

        var currentCursor = metadata.cursor
        var totalSynced = 0

        // Paginate through the sync endpoint until we've caught up
        do {
            var hasMore = false

            messageApiRepository.syncMessages(
                conversationId = conversationId,
                lastMessageId = currentCursor,
                limit = SYNC_PAGE_SIZE,
            ).fold(
                onSuccess = { response ->
                    if (response.messages.isNotEmpty()) {
                        val entities = response.messages.map { it.toEntity(currentUserId) }
                        messageDao.upsertAll(entities)
                        totalSynced += response.messages.size

                        // Advance cursor to the latest message we just received
                        currentCursor = response.messages.last().id
                    }
                    hasMore = response.hasMore
                },
                onFailure = { exception ->
                    // Save progress even on failure — we'll resume from the last successful cursor
                    if (totalSynced > 0) {
                        syncMetadataDao.upsert(
                            metadata.copy(
                                cursor = currentCursor,
                                lastSuccessEpochMs = System.currentTimeMillis(),
                            )
                        )
                    }
                    syncMetadataDao.incrementFailureCount(resourceKey)
                    Timber.e(
                        "SyncEngine: Incremental sync failed for %s after %d messages: %s",
                        conversationId, totalSynced, exception.throwable.message,
                    )
                    throw exception.throwable
                },
            )
        } while (hasMore)

        // Update sync metadata with the final cursor position
        val latestTimestamp = if (totalSynced > 0) {
            // We synced new messages — update the timestamp
            messageDao.getRecentMessages(conversationId, limit = 1)
                .firstOrNull()?.createdAt ?: metadata.lastSyncedAt
        } else {
            metadata.lastSyncedAt
        }

        syncMetadataDao.upsert(
            metadata.copy(
                lastSyncedAt = latestTimestamp,
                cursor = currentCursor,
                failureCount = 0,
                lastSuccessEpochMs = System.currentTimeMillis(),
            )
        )
        Timber.d("SyncEngine: Incremental sync complete — %d new messages, cursor=%s", totalSynced, currentCursor)
    }

    // ── Targeted Sync ───────────────────────────────────────────────────

    override suspend fun syncTargeted(conversations: List<SyncConversation>) {
        withContext(ioDispatcher) {
            Timber.d("SyncEngine: Targeted sync for %d conversations", conversations.size)

            // Refresh conversation list to get updated metadata (participants, last_message, etc.)
            syncConversations()

            // Sync messages for each flagged conversation using the /sync endpoint
            for (conv in conversations) {
                try {
                    syncMessages(conv.conversationId)
                    // Apply the authoritative unread count from the server
                    conversationDao.setUnreadCount(conv.conversationId, conv.unreadCount)
                } catch (e: Exception) {
                    // Don't let one failed conversation block the others
                    Timber.e(e, "SyncEngine: Targeted sync failed for conversation %s", conv.conversationId)
                }
            }
        }
    }

    // ── Pagination (Load Older) ─────────────────────────────────────────

    override suspend fun loadOlderMessages(conversationId: String): Boolean {
        return withContext(ioDispatcher) {
            // Get the oldest message we currently have for this conversation
            val oldestMessages = messageDao.getOldestMessages(conversationId, limit = 1)
            val oldestTimestamp = oldestMessages.firstOrNull()?.createdAt

            if (oldestTimestamp == null) {
                // No messages at all — do an initial sync instead
                syncMessages(conversationId)
                return@withContext true
            }

            Timber.d("SyncEngine: Loading older messages for %s before %s", conversationId, oldestTimestamp)

            var hasMore = false
            messageApiRepository.getMessages(
                conversationId = conversationId,
                before = oldestTimestamp,
                limit = PAGE_SIZE,
            ).fold(
                onSuccess = { response ->
                    if (response.messages.isNotEmpty()) {
                        val entities = response.messages.map { it.toEntity(currentUserId) }
                        messageDao.upsertAll(entities)
                    }
                    hasMore = response.hasMore
                    Timber.d(
                        "SyncEngine: Loaded %d older messages, hasMore=%b",
                        response.messages.size, hasMore,
                    )
                },
                onFailure = { exception ->
                    Timber.e("SyncEngine: Load older messages failed for %s: %s", conversationId, exception.throwable.message)
                    throw exception.throwable
                },
            )
            hasMore
        }
    }

    // ── Staleness ───────────────────────────────────────────────────────

    override suspend fun isStale(resourceType: String, staleThresholdMs: Long): Boolean {
        val metadata = syncMetadataDao.get(resourceType) ?: return true
        if (!metadata.initialSyncComplete) return true
        val elapsed = System.currentTimeMillis() - metadata.lastSuccessEpochMs
        return elapsed > staleThresholdMs
    }

    // ── Reset ───────────────────────────────────────────────────────────

    override suspend fun reset() {
        withContext(ioDispatcher) {
            syncMetadataDao.deleteAll()
            syncLocks.clear()
            Timber.d("SyncEngine: Reset all sync metadata")
        }
    }

    companion object {
        private const val INITIAL_MESSAGE_FETCH_LIMIT = 50
        private const val SYNC_PAGE_SIZE = 100
        private const val PAGE_SIZE = 30
    }
}

/**
 * Manages per-resource mutex locks to prevent concurrent syncs of the same resource
 * from racing against each other. Different resources can sync in parallel.
 */
private class SyncLockManager {
    private val locks = mutableMapOf<String, Mutex>()
    private val mapLock = Mutex()

    suspend fun <T> withLock(key: String, block: suspend () -> T): T {
        val mutex = mapLock.withLock {
            locks.getOrPut(key) { Mutex() }
        }
        return mutex.withLock { block() }
    }

    suspend fun clear() {
        mapLock.withLock { locks.clear() }
    }
}
