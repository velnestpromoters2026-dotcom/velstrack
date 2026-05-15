package com.velstrack.app.presentation.employee.dialer.service

import android.telecom.Call
import android.telecom.VideoProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CallManager {

    private val _callState = MutableStateFlow<Call?>(null)
    val callState: StateFlow<Call?> = _callState.asStateFlow()

    private val _callStateInt = MutableStateFlow<Int>(Call.STATE_DISCONNECTED)
    val callStateInt: StateFlow<Int> = _callStateInt.asStateFlow()

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            _callStateInt.value = state
            
            if (state == Call.STATE_DISCONNECTED) {
                _callState.value = null
            }
        }
    }

    fun updateCall(call: Call?) {
        _callState.value?.unregisterCallback(callCallback)
        
        _callState.value = call
        if (call != null) {
            _callStateInt.value = call.state
            call.registerCallback(callCallback)
        } else {
            _callStateInt.value = Call.STATE_DISCONNECTED
        }
    }

    fun endCall() {
        _callState.value?.disconnect()
    }

    fun answerCall() {
        _callState.value?.answer(VideoProfile.STATE_AUDIO_ONLY)
    }

    fun rejectCall() {
        _callState.value?.reject(false, null)
    }
}
