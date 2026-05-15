package com.velstrack.app.domain.telecom

import android.util.Log
import com.velstrack.app.data.local.dao.CallDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRecoveryManager @Inject constructor(
    private val callDao: CallDao
) {

    suspend fun recoverOrphanedSessions() {
        withContext(Dispatchers.IO) {
            // Find sessions stuck in STARTED, DIALING, ACTIVE for more than 2 hours
            val twoHoursAgo = System.currentTimeMillis() - (2 * 60 * 60 * 1000)
            val orphaned = callDao.getOrphanedSessions(twoHoursAgo)
            
            if (orphaned.isNotEmpty()) {
                Log.w("SessionRecoveryManager", "Found ${orphaned.size} orphaned sessions. Marking as FAILED.")
                val failedSessions = orphaned.map {
                    it.copy(
                        sessionState = "FAILED",
                        callVerified = false
                    )
                }
                callDao.insertCalls(failedSessions)
            }
        }
    }
}
