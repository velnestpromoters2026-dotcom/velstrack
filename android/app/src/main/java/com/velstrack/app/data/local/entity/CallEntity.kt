package com.velstrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calls")
data class CallEntity(
    @PrimaryKey val id: String, // Hash + timestamp to avoid duplicates
    val clientPhoneHash: String,
    val durationSeconds: Int,
    val callType: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)
