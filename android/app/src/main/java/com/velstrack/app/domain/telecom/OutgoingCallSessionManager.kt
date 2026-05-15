package com.velstrack.app.domain.telecom

import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.data.local.entity.CallEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutgoingCallSessionManager @Inject constructor(
    private val callDao: CallDao
) {

    suspend fun startSession(employeeId: String, rawNumber: String, startEpochMillis: Long): String {
        return withContext(Dispatchers.IO) {
            val normalizedDbNumber = rawNumber.replace(Regex("[^0-9+]"), "")
            // Use precise timestamp to ensure unique session ID
            val sessionId = "${normalizedDbNumber}_${startEpochMillis}"
            
            // Dummy fingerprint to satisfy unique index, will be overwritten upon verification
            val dummyFingerprint = "temp_${sessionId}"
            
            val initialSession = CallEntity(
                id = sessionId,
                callFingerprint = dummyFingerprint,
                clientPhoneHash = rawNumber, // Unhashed initially
                durationSeconds = 0,
                callType = "OUTGOING",
                timestamp = startEpochMillis,
                isSynced = false,
                connectedAtMillis = null,
                disconnectedAtMillis = null,
                sessionState = "STARTED",
                callVerified = false
            )
            
            callDao.insertCalls(listOf(initialSession))
            
            sessionId
        }
    }
}
