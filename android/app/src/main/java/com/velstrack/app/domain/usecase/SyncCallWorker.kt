package com.velstrack.app.domain.usecase

import android.content.Context
import android.provider.CallLog
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.data.local.entity.CallEntity
import com.velstrack.app.data.remote.api.ApiService
import com.velstrack.app.data.remote.dto.SyncCallDto
import com.velstrack.app.data.remote.dto.SyncCallRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.Calendar

@HiltWorker
class SyncCallWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val callDao: CallDao,
    private val apiService: ApiService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("SyncCallWorker", "Starting Call Sync...")
            
            // We no longer scan the entire Android CallLog here.
            // Calls are now exclusively tracked and inserted into Room 
            // by the EmployeeDashboardViewModel when dialed from the app.


            // 4. Fetch Unsynced
            val unsynced = callDao.getUnsyncedCalls()
            if (unsynced.isEmpty()) {
                Log.d("SyncCallWorker", "No unsynced calls. Done.")
                return@withContext Result.success()
            }

            // 5. Sync to API
            val dtos = unsynced.map {
                SyncCallDto(
                    clientPhoneHash = it.clientPhoneHash,
                    durationSeconds = it.durationSeconds,
                    callType = it.callType,
                    timestamp = it.timestamp,
                    callFingerprint = it.callFingerprint
                )
            }

            val request = SyncCallRequest(calls = dtos)
            val response = apiService.syncCalls(request)

            if (response.isSuccessful && response.body()?.success == true) {
                // 6. Mark synced
                callDao.markAsSynced(unsynced.map { it.id })
                Log.d("SyncCallWorker", "Successfully synced ${unsynced.size} calls.")
                Result.success()
            } else {
                Log.e("SyncCallWorker", "API returned error: ${response.code()}")
                Result.retry()
            }
        } catch (e: SecurityException) {
            Log.e("SyncCallWorker", "Missing READ_CALL_LOG permission.")
            Result.failure() // Can't retry without permission
        } catch (e: Exception) {
            Log.e("SyncCallWorker", "Sync failed: ${e.message}")
            Result.retry()
        }
    }

    private fun hashPhoneNumber(number: String): String {
        return number
    }
}
