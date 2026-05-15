package com.velstrack.app.presentation.employee.dialer.service

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.data.local.entity.CallEntity
import com.velstrack.app.core.datastore.SessionManager
import com.velstrack.app.domain.usecase.SyncCallWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@AndroidEntryPoint
class VelstrackInCallService : InCallService() {

    @Inject
    lateinit var callDao: CallDao

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        CallManager.inCallService = this
        Log.d("VelstrackInCallService", "Call Added: ${call?.state}")
        
        call?.let {
            CallManager.updateCall(it)
            
            // If it's an incoming call, or if the user dialled, we want to launch our UI
            // but for now, we'll just let the DialerScreen handle navigation to ActiveCallScreen
            // or we could launch the activity here via Intent.
            val number = it.details?.handle?.schemeSpecificPart ?: "Unknown"
            
            // Send broadcast or launch activity to show call UI
            // Actually, best practice is to launch an Activity when a call is added
            val intent = Intent(this, com.velstrack.app.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("call_number", number)
                putExtra("call_action", "show_active_call")
            }
            startActivity(intent)
        }
    }

    override fun onCallRemoved(call: Call?) {
        super.onCallRemoved(call)
        Log.d("VelstrackInCallService", "Call Removed")
        
        call?.let {
            val number = it.details?.handle?.schemeSpecificPart ?: "Unknown"
            val connectTime = it.details?.connectTimeMillis ?: 0L
            val disconnectTime = System.currentTimeMillis()
            
            val durationSeconds = if (connectTime > 0) {
                ((disconnectTime - connectTime) / 1000).toInt()
            } else {
                0
            }
            
            // Extract exact state and log to database immediately
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val employeeId = sessionManager.getUserId().firstOrNull() ?: "UNKNOWN_EMP"
                    val date = if (connectTime > 0) connectTime else disconnectTime
                    val normalizedDbNumber = number.replace(Regex("[^0-9+]"), "")

                    val rawFingerprint = "${employeeId}${normalizedDbNumber}${date}${durationSeconds}"
                    val digest = MessageDigest.getInstance("SHA-256")
                    val hashBytes = digest.digest(rawFingerprint.toByteArray(Charsets.UTF_8))
                    val fingerprint = hashBytes.joinToString("") { "%02x".format(it) }

                    val id = "${number}_${date}"
                    val matchedCall = CallEntity(
                        id = id,
                        callFingerprint = fingerprint,
                        clientPhoneHash = number,
                        durationSeconds = durationSeconds,
                        callType = "OUTGOING",
                        timestamp = date,
                        isSynced = false
                    )

                    callDao.insertCalls(listOf(matchedCall))
                    
                    // Trigger background sync worker
                    val workRequest = OneTimeWorkRequestBuilder<SyncCallWorker>().build()
                    WorkManager.getInstance(applicationContext).enqueue(workRequest)
                    
                    // Clear pending call preference so dashboard doesn't double-log it
                    val prefs = applicationContext.getSharedPreferences("velstrack_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit()
                        .remove("pending_call_number")
                        .remove("pending_call_time")
                        .apply()
                        
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        CallManager.updateCall(null)
        CallManager.inCallService = null
    }
}
