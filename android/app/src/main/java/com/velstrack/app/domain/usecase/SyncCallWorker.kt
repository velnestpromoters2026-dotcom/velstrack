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
            
            // 1. Get midnight timestamp
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startOfDay = calendar.timeInMillis

            // 2. Query CallLog
            val cursor = applicationContext.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DATE
                ),
                "${CallLog.Calls.DATE} >= ?",
                arrayOf(startOfDay.toString()),
                "${CallLog.Calls.DATE} DESC"
            )

            val newEntities = mutableListOf<CallEntity>()

            cursor?.use { c ->
                val numberIdx = c.getColumnIndex(CallLog.Calls.NUMBER)
                val durationIdx = c.getColumnIndex(CallLog.Calls.DURATION)
                val typeIdx = c.getColumnIndex(CallLog.Calls.TYPE)
                val dateIdx = c.getColumnIndex(CallLog.Calls.DATE)

                while (c.moveToNext()) {
                    val number = c.getString(numberIdx) ?: continue
                    val duration = c.getInt(durationIdx)
                    val typeInt = c.getInt(typeIdx)
                    val date = c.getLong(dateIdx)

                    val typeStr = when (typeInt) {
                        CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                        CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                        CallLog.Calls.MISSED_TYPE -> "MISSED"
                        CallLog.Calls.REJECTED_TYPE -> "REJECTED"
                        else -> "UNKNOWN"
                    }

                    // Only track Outgoing and Incoming
                    if (typeStr == "UNKNOWN" || typeStr == "MISSED" || typeStr == "REJECTED") continue

                    val phoneHash = hashPhoneNumber(number)
                    val id = "${phoneHash}_${date}" // Unique per call

                    newEntities.add(
                        CallEntity(
                            id = id,
                            clientPhoneHash = phoneHash,
                            durationSeconds = duration,
                            callType = typeStr,
                            timestamp = date,
                            isSynced = false
                        )
                    )
                }
            }

            // 3. Save to Room
            if (newEntities.isNotEmpty()) {
                callDao.insertCalls(newEntities)
            }

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
                    timestamp = it.timestamp
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
        // Strip non-digits
        val digitsOnly = number.replace(Regex("[^0-9]"), "")
        // Extremely simple hashing for demo/privacy purposes: SHA-256
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(digitsOnly.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
