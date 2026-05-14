package com.velstrack.app.data.remote.dto

data class SyncCallRequest(
    val calls: List<SyncCallDto>
)

data class SyncCallDto(
    val clientPhoneHash: String,
    val durationSeconds: Int,
    val callType: String,
    val timestamp: Long,
    val isVelstrackCall: Boolean = true
)
