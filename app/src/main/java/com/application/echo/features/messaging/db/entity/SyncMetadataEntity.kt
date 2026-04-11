package com.application.echo.features.messaging.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks sync state per resource. Enables incremental sync by storing
 * the last-known server cursor/timestamp so subsequent fetches only
 * pull what changed.
 *
 * [resourceType] examples: "conversations", "messages:<conversationId>"
 */
@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val resourceType: String,
    /** ISO-8601 timestamp of the most recent item we have from the server. */
    val lastSyncedAt: String?,
    /** Server-provided cursor for paginated endpoints. */
    val cursor: String?,
    /** Whether the initial full fetch has completed for this resource. */
    val initialSyncComplete: Boolean,
    /** Number of consecutive sync failures (reset on success). */
    val failureCount: Int = 0,
    /** Epoch millis of last successful sync (for staleness checks). */
    val lastSuccessEpochMs: Long = 0L,
)
