package com.application.echo.features.messaging.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.application.echo.features.messaging.db.entity.ParticipantEntity

@Dao
interface ParticipantDao {

    @Upsert
    suspend fun upsertAll(participants: List<ParticipantEntity>)

    @Query("SELECT * FROM participants WHERE conversationId = :conversationId")
    suspend fun getByConversation(conversationId: String): List<ParticipantEntity>

    @Query("UPDATE participants SET onlineStatus = :status WHERE userId = :userId")
    suspend fun updatePresence(userId: String, status: String)

    @Query("DELETE FROM participants")
    suspend fun deleteAll()
}
