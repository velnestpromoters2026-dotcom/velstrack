package com.velstrack.app.domain.telecom

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.velstrack.app.core.datastore.SessionManager
import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.domain.usecase.SyncCallWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VerifiedCallExtractor @Inject constructor(
    private val callDao: CallDao,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) {

    suspend fun finalizeSession(sessionId: String, disconnectedAtMillis: Long) {
        withContext(Dispatchers.IO) {
            val session = callDao.getCallById(sessionId)
            
            if (session == null) {
                Log.e("VerifiedCallExtractor", "Orphaned or missing session: $sessionId")
                return@withContext
            }
            
            // Recompute duration directly from our internal cached epochs
            val connectTime = session.connectedAtMillis ?: session.timestamp
            val durationSeconds = ((disconnectedAtMillis - connectTime) / 1000).toInt()
            
            // Only verify if duration is reasonable (e.g. >= 0)
            val isVerified = durationSeconds >= 0
            
            val employeeId = sessionManager.getUserId().firstOrNull() ?: "UNKNOWN_EMP"
            val normalizedDbNumber = session.clientPhoneHash.replace(Regex("[^0-9+]"), "")
            
            // Secure final fingerprint
            val rawFingerprint = "${employeeId}${normalizedDbNumber}${connectTime}${durationSeconds}"
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(rawFingerprint.toByteArray(Charsets.UTF_8))
            val fingerprint = hashBytes.joinToString("") { "%02x".format(it) }

            val finalSession = session.copy(
                disconnectedAtMillis = disconnectedAtMillis,
                durationSeconds = durationSeconds,
                sessionState = "DISCONNECTED",
                callVerified = isVerified,
                callFingerprint = fingerprint
            )
            
            callDao.insertCalls(listOf(finalSession))
            
            if (isVerified) {
                Log.d("VerifiedCallExtractor", "Call verified: $durationSeconds seconds. Queueing sync.")
                val workRequest = OneTimeWorkRequestBuilder<SyncCallWorker>().build()
                WorkManager.getInstance(context).enqueue(workRequest)
            } else {
                Log.w("VerifiedCallExtractor", "Call validation failed. Duration: $durationSeconds")
            }
        }
    }
}
