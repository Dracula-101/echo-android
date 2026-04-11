package com.application.echo.features.messaging.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.application.echo.features.messaging.db.entity.PendingMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingMessageDao {

    @Query("SELECT * FROM pending_messages WHERE status = 'queued' ORDER BY createdAt ASC")
    fun observeQueued(): Flow<List<PendingMessageEntity>>

    @Query("SELECT * FROM pending_messages WHERE status = 'queued' ORDER BY createdAt ASC")
    suspend fun getQueued(): List<PendingMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: PendingMessageEntity)

    @Query("UPDATE pending_messages SET status = :status WHERE localId = :localId")
    suspend fun setStatus(localId: String, status: String)

    @Query("UPDATE pending_messages SET status = 'queued', retryCount = retryCount + 1 WHERE localId = :localId")
    suspend fun requeue(localId: String)

    @Query("UPDATE pending_messages SET status = 'queued', retryCount = 0 WHERE localId = :localId")
    suspend fun retryNow(localId: String)

    @Query("DELETE FROM pending_messages WHERE localId = :localId")
    suspend fun delete(localId: String)

    @Query("DELETE FROM pending_messages")
    suspend fun deleteAll()
}
