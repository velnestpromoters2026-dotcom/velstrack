package com.velstrack.app.presentation.employee.dialer.service

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.velstrack.app.core.datastore.SessionManager
import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.domain.telecom.OutgoingCallSessionManager
import com.velstrack.app.domain.telecom.VerifiedCallExtractor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class VelstrackInCallService : InCallService() {

    @Inject
    lateinit var sessionManager: SessionManager
    
    @Inject
    lateinit var callDao: CallDao
    
    @Inject
    lateinit var outgoingCallSessionManager: OutgoingCallSessionManager
    
    @Inject
    lateinit var verifiedCallExtractor: VerifiedCallExtractor
    
    private var currentSessionId: String? = null
    
    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            
            CoroutineScope(Dispatchers.IO).launch {
                currentSessionId?.let { sessionId ->
                    val session = callDao.getCallById(sessionId) ?: return@launch
                    
                    if (state == Call.STATE_ACTIVE && session.sessionState != "ACTIVE") {
                        val activeSession = session.copy(
                            sessionState = "ACTIVE",
                            connectedAtMillis = System.currentTimeMillis()
                        )
                        callDao.insertCalls(listOf(activeSession))
                        Log.d("VelstrackInCallService", "Session $sessionId moved to ACTIVE")
                    } else if (state == Call.STATE_DISCONNECTED && session.sessionState != "DISCONNECTED") {
                        verifiedCallExtractor.finalizeSession(sessionId, System.currentTimeMillis())
                        Log.d("VelstrackInCallService", "Session $sessionId moved to DISCONNECTED and finalized")
                    }
                }
            }
        }
    }

    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        CallManager.inCallService = this
        Log.d("VelstrackInCallService", "Call Added: ${call?.state}")
        
        // Start Foreground Service to prevent OS killing
        val serviceIntent = Intent(this, ForegroundTrackingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        
        call?.let {
            CallManager.updateCall(it)
            it.registerCallback(callCallback)
            
            val prefs = applicationContext.getSharedPreferences("velstrack_prefs", android.content.Context.MODE_PRIVATE)
            val pendingNumber = prefs.getString("pending_call_number", null)
            val pendingTime = prefs.getLong("pending_call_time", System.currentTimeMillis())
            val number = pendingNumber ?: it.details?.handle?.schemeSpecificPart ?: "Unknown"
            
            CoroutineScope(Dispatchers.IO).launch {
                val employeeId = sessionManager.getUserId().firstOrNull() ?: "UNKNOWN_EMP"
                currentSessionId = outgoingCallSessionManager.startSession(employeeId, number, pendingTime)
            }
            
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
        
        call?.unregisterCallback(callCallback)
        
        // Stop Foreground Service
        val serviceIntent = Intent(this, ForegroundTrackingService::class.java).apply {
            action = ForegroundTrackingService.ACTION_STOP
        }
        startService(serviceIntent)
        
        // Cleanup preferences
        val prefs = applicationContext.getSharedPreferences("velstrack_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .remove("pending_call_number")
            .remove("pending_call_time")
            .apply()
            
        CallManager.updateCall(null)
        CallManager.inCallService = null
        currentSessionId = null
    }
}
