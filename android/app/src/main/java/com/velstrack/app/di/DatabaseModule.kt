package com.velstrack.app.di

import android.content.Context
import androidx.room.Room
import com.velstrack.app.data.local.AppDatabase
import com.velstrack.app.data.local.dao.CallDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "velstrack_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideCallDao(appDatabase: AppDatabase): CallDao {
        return appDatabase.callDao()
    }
}
