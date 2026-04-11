package com.application.echo.features.messaging.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.application.echo.features.messaging.db.entity.SyncMetadataEntity

@Dao
interface SyncMetadataDao {

    @Query("SELECT * FROM sync_metadata WHERE resourceType = :resourceType")
    suspend fun get(resourceType: String): SyncMetadataEntity?

    @Upsert
    suspend fun upsert(metadata: SyncMetadataEntity)

    @Query("UPDATE sync_metadata SET failureCount = failureCount + 1 WHERE resourceType = :resourceType")
    suspend fun incrementFailureCount(resourceType: String)

    @Query("DELETE FROM sync_metadata WHERE resourceType = :resourceType")
    suspend fun delete(resourceType: String)

    @Query("DELETE FROM sync_metadata")
    suspend fun deleteAll()
}
