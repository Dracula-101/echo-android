package com.application.echo.features.messaging.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.application.echo.features.messaging.db.entity.ConversationEntity
import com.application.echo.features.messaging.db.relation.ConversationWithParticipants
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Transaction
    @Query("SELECT * FROM conversations ORDER BY lastMessageTimestamp DESC")
    fun observeAllWithParticipants(): Flow<List<ConversationWithParticipants>>

    @Transaction
    @Query("SELECT * FROM conversations WHERE id = :id")
    fun observeById(id: String): Flow<ConversationWithParticipants?>

    @Query("SELECT COALESCE(SUM(unreadCount), 0) FROM conversations")
    fun observeTotalUnreadCount(): Flow<Int>

    @Query("SELECT COALESCE(unreadCount, 0) FROM conversations WHERE id = :id")
    fun observeUnreadCount(id: String): Flow<Int>

    @Upsert
    suspend fun upsertAll(conversations: List<ConversationEntity>)

    @Upsert
    suspend fun upsert(conversation: ConversationEntity)

    @Query("UPDATE conversations SET unreadCount = :count WHERE id = :id")
    suspend fun setUnreadCount(id: String, count: Int)

    @Query("UPDATE conversations SET unreadCount = unreadCount + 1 WHERE id = :id")
    suspend fun incrementUnreadCount(id: String)

    @Query(
        """UPDATE conversations SET
        lastMessageId = :msgId, lastMessageContent = :content,
        lastMessageSenderId = :senderId, lastMessageTimestamp = :timestamp,
        lastMessageStatus = :status WHERE id = :conversationId"""
    )
    suspend fun updateLastMessage(
        conversationId: String,
        msgId: String,
        content: String?,
        senderId: String,
        timestamp: String,
        status: String?,
    )

    @Query("DELETE FROM conversations")
    suspend fun deleteAll()
}
