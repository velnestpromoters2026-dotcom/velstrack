package com.velstrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "calls",
    indices = [androidx.room.Index(value = ["callFingerprint"], unique = true)]
)
data class CallEntity(
    @PrimaryKey val id: String, 
    val callFingerprint: String,
    val clientPhoneHash: String,
    val durationSeconds: Int,
    val callType: String,
    val timestamp: Long,
    val isSynced: Boolean = false,
    val connectedAtMillis: Long? = null,
    val disconnectedAtMillis: Long? = null,
    val sessionState: String = "STARTED",
    val callVerified: Boolean = false
)
