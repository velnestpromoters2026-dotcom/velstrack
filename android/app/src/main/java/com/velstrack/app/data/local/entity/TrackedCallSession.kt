package com.velstrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracked_call_sessions",
    indices = [androidx.room.Index(value = ["callFingerprint"], unique = true)]
)
data class TrackedCallSession(
    @PrimaryKey val sessionId: String,
    val employeeId: String,
    val phoneNumber: String,
    val startedAt: Long,
    val status: String = "PENDING", // PENDING, VERIFIED, DUPLICATE, FAILED
    val synced: Boolean = false,
    
    // Extracted Fields (populated after verification)
    val contactName: String? = null,
    val callDate: String? = null, // "14 May 2026"
    val callTime: String? = null, // "07:22 AM"
    val durationSeconds: Int? = null,
    val callType: String? = null,
    val callFingerprint: String? = null
)
