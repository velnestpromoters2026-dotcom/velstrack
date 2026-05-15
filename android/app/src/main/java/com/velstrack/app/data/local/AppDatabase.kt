package com.velstrack.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.data.local.entity.CallEntity

@Database(entities = [CallEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callDao(): CallDao
}
