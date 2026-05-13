package com.velstrack.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VelstrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization for WorkManager, Timber, etc.
    }
}
