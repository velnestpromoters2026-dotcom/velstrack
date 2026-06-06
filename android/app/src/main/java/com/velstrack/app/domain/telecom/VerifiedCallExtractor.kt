package com.velstrack.app.domain.telecom

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.velstrack.app.core.datastore.SessionManager
import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.domain.usecase.SyncCallWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VerifiedCallExtractor @Inject constructor(
    private val callDao: CallDao,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) {

    suspend fun finalizeSession(sessionId: String, disconnectedAtMillis: Long) {
        // No longer used since we verify instantly on start.
    }
}
