package com.velstrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.velstrack.app.data.local.entity.CallEntity
import com.velstrack.app.data.local.entity.TrackedCallSession
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalls(calls: List<CallEntity>)

    @Query("SELECT * FROM calls WHERE id = :id LIMIT 1")
    suspend fun getCallById(id: String): CallEntity?

    @Query("SELECT * FROM calls WHERE isSynced = 0 AND callVerified = 1")
    suspend fun getUnsyncedCalls(): List<CallEntity>

    @Query("SELECT * FROM calls WHERE sessionState IN ('STARTED', 'DIALING', 'ACTIVE') AND timestamp < :olderThanMillis")
    suspend fun getOrphanedSessions(olderThanMillis: Long): List<CallEntity>

    @Query("SELECT * FROM calls ORDER BY timestamp DESC")
    suspend fun getAllCalls(): List<CallEntity>

    @Query("UPDATE calls SET isSynced = 1 WHERE id IN (:callIds)")
    suspend fun markAsSynced(callIds: List<String>)

    @Query("SELECT SUM(durationSeconds) FROM calls WHERE timestamp >= :startOfDay AND callType = 'OUTGOING'")
    fun getTodayOutboundDuration(startOfDay: Long): Flow<Int?>

    // TrackedCallSession queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackedCallSession(session: TrackedCallSession)

    @Query("SELECT * FROM tracked_call_sessions WHERE status = 'PENDING' ORDER BY startedAt DESC")
    suspend fun getPendingSessions(): List<TrackedCallSession>

    @Query("SELECT * FROM tracked_call_sessions WHERE synced = 0 AND status = 'VERIFIED'")
    suspend fun getUnsyncedSessions(): List<TrackedCallSession>

    @Query("UPDATE tracked_call_sessions SET synced = 1 WHERE sessionId IN (:sessionIds)")
    suspend fun markSessionsAsSynced(sessionIds: List<String>)

    @Query("SELECT * FROM tracked_call_sessions WHERE status = 'VERIFIED' ORDER BY startedAt DESC")
    suspend fun getAllVerifiedSessions(): List<TrackedCallSession>
}
