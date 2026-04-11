package com.application.echo.features.messaging.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.application.echo.features.messaging.db.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query(
        """SELECT * FROM messages WHERE conversationId = :conversationId
        ORDER BY createdAt ASC"""
    )
    fun observeByConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query(
        """SELECT * FROM messages WHERE conversationId = :conversationId
        ORDER BY createdAt DESC LIMIT :limit"""
    )
    suspend fun getRecentMessages(conversationId: String, limit: Int = 50): List<MessageEntity>

    @Query(
        """SELECT * FROM messages WHERE conversationId = :conversationId
        ORDER BY createdAt ASC LIMIT :limit"""
    )
    suspend fun getOldestMessages(conversationId: String, limit: Int = 1): List<MessageEntity>

    @Upsert
    suspend fun upsertAll(messages: List<MessageEntity>)

    @Upsert
    suspend fun upsert(message: MessageEntity)

    @Query("UPDATE messages SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query(
        """UPDATE messages SET status = 'read'
        WHERE conversationId = :conversationId AND id IN (:ids)"""
    )
    suspend fun markAsRead(conversationId: String, ids: List<String>)

    @Query("DELETE FROM messages WHERE localId = :localId")
    suspend fun deleteByLocalId(localId: String)

    @Transaction
    suspend fun replaceOptimistic(localId: String, confirmed: MessageEntity) {
        deleteByLocalId(localId)
        upsert(confirmed)
    }

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteByConversation(conversationId: String)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()
}
