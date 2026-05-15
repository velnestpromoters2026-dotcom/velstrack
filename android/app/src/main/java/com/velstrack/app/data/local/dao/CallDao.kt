package com.velstrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.velstrack.app.data.local.entity.CallEntity
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

    @Query("UPDATE calls SET isSynced = 1 WHERE id IN (:callIds)")
    suspend fun markAsSynced(callIds: List<String>)

    @Query("SELECT SUM(durationSeconds) FROM calls WHERE timestamp >= :startOfDay AND callType = 'OUTGOING'")
    fun getTodayOutboundDuration(startOfDay: Long): Flow<Int?>
}
