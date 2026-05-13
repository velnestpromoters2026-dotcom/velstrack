package com.velstrack.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.data.local.entity.CallEntity

@Database(entities = [CallEntity::class], version = 1, exportSchema = false)
abstract class VelstrackDatabase : RoomDatabase() {
    abstract fun callDao(): CallDao
}
