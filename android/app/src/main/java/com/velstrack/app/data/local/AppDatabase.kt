package com.velstrack.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.data.local.entity.CallEntity
import com.velstrack.app.data.local.entity.TrackedCallSession

@Database(entities = [CallEntity::class, TrackedCallSession::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callDao(): CallDao
}
