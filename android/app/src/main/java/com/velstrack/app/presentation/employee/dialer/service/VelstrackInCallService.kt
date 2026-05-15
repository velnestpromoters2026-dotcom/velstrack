package com.velstrack.app.presentation.employee.dialer.service

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log

class VelstrackInCallService : InCallService() {

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
        CallManager.updateCall(null)
        CallManager.inCallService = null
    }
}
