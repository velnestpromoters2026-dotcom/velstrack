package com.velstrack.app.presentation.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.velstrack.app.core.util.UiState
import com.velstrack.app.data.remote.dto.EmployeeDashboardDto
import com.velstrack.app.domain.repository.EmployeeRepository
import com.velstrack.app.domain.usecase.SyncCallWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.provider.CallLog
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.data.local.entity.CallEntity
import com.velstrack.app.data.remote.api.ApiService
import com.velstrack.app.core.datastore.SessionManager
import com.velstrack.app.data.remote.dto.SyncCallDto
import com.velstrack.app.data.remote.dto.SyncCallRequest
import java.security.MessageDigest
import java.util.Calendar

@HiltViewModel
class EmployeeDashboardViewModel @Inject constructor(
    private val repository: EmployeeRepository,
    private val workManager: WorkManager,
    private val callDao: CallDao,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<UiState<EmployeeDashboardDto>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<EmployeeDashboardDto>> = _dashboardState

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        _dashboardState.value = UiState.Loading
        viewModelScope.launch {
            repository.getDashboardStats().collect { result ->
                if (result.isSuccess) {
                    _dashboardState.value = UiState.Success(result.getOrNull()!!)
                } else {
                    _dashboardState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun startCallSyncWorker() {
        val syncRequest = PeriodicWorkRequestBuilder<SyncCallWorker>(15, TimeUnit.MINUTES)
            .build()
            
        workManager.enqueueUniquePeriodicWork(
            "CallSyncWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        // Run background worker as fallback
        val immediateRequest = androidx.work.OneTimeWorkRequestBuilder<SyncCallWorker>().build()
        workManager.enqueueUniqueWork(
            "CallSyncWorker_Immediate",
            androidx.work.ExistingWorkPolicy.KEEP,
            immediateRequest
        )
    }

    private fun hashPhoneNumber(number: String): String {
        // Return raw number instead of hashing so it shows up correctly in the UI
        return number
    }

    suspend fun syncCallsNowAndLoad() {
        _dashboardState.value = UiState.Loading
        withContext(Dispatchers.IO) {
            try {
                // Add a small delay to allow native Android dialer to write to CallLog database
                kotlinx.coroutines.delay(2000)
                
                val prefs = context.getSharedPreferences("velstrack_prefs", Context.MODE_PRIVATE)
                val pendingNumber = prefs.getString("pending_call_number", null)
                val pendingCallTime = prefs.getLong("pending_call_time", 0L)

                if (pendingNumber != null) {
                    val cursor = context.contentResolver.query(
                        CallLog.Calls.CONTENT_URI,
                        arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.DATE),
                        "${CallLog.Calls.DATE} >= ?",
                        arrayOf(pendingCallTime.toString()),
                        "${CallLog.Calls.DATE} DESC"
                    )

                    val employeeId = sessionManager.getUserId().firstOrNull() ?: "UNKNOWN_EMP"
                    var matchedCall: CallEntity? = null

                    cursor?.use { c ->
                        val numberIdx = c.getColumnIndex(CallLog.Calls.NUMBER)
                        val durationIdx = c.getColumnIndex(CallLog.Calls.DURATION)
                        val typeIdx = c.getColumnIndex(CallLog.Calls.TYPE)
                        val dateIdx = c.getColumnIndex(CallLog.Calls.DATE)

                        while (c.moveToNext()) {
                            val number = c.getString(numberIdx) ?: continue
                            // Check if this call matches our pending number (strip formatting)
                            val normalizedDbNumber = number.replace(Regex("[^0-9+]"), "")
                            val normalizedPending = pendingNumber.replace(Regex("[^0-9+]"), "")
                            
                            if (normalizedDbNumber == normalizedPending || normalizedDbNumber.contains(normalizedPending) || normalizedPending.contains(normalizedDbNumber)) {
                                val duration = c.getInt(durationIdx)
                                val typeInt = c.getInt(typeIdx)
                                val date = c.getLong(dateIdx)

                                val typeStr = when (typeInt) {
                                    CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                                    else -> "UNKNOWN"
                                }

                                if (typeStr == "OUTGOING") {
                                    val rawFingerprint = "${employeeId}${normalizedDbNumber}${date}${duration}"
                                    val digest = MessageDigest.getInstance("SHA-256")
                                    val hashBytes = digest.digest(rawFingerprint.toByteArray(Charsets.UTF_8))
                                    val fingerprint = hashBytes.joinToString("") { "%02x".format(it) }

                                    val id = "${hashPhoneNumber(number)}_${date}"
                                    matchedCall = CallEntity(
                                        id = id,
                                        callFingerprint = fingerprint,
                                        clientPhoneHash = hashPhoneNumber(number),
                                        durationSeconds = duration,
                                        callType = typeStr,
                                        timestamp = date,
                                        isSynced = false
                                    )
                                    break // Found the most recent matching call!
                                }
                            }
                        }
                    }

                    if (matchedCall != null) {
                        callDao.insertCalls(listOf(matchedCall!!))
                        // Clear the pending number so we don't process it again
                        prefs.edit()
                            .remove("pending_call_number")
                            .remove("pending_call_time")
                            .apply()
                    } else {
                        // Wait for system to write to CallLog, do not clear
                    }
                }

                // Now sync any unsynced calls from Room
                val unsyncedCalls = callDao.getUnsyncedCalls()
                if (unsyncedCalls.isNotEmpty()) {
                    val dtos = unsyncedCalls.map {
                        SyncCallDto(
                            clientPhoneHash = it.clientPhoneHash,
                            durationSeconds = it.durationSeconds,
                            callType = it.callType,
                            timestamp = it.timestamp,
                            callFingerprint = it.callFingerprint,
                            isVelstrackCall = true
                        )
                    }

                    val request = SyncCallRequest(calls = dtos)
                    val response = apiService.syncCalls(request)

                    if (response.isSuccessful && response.body()?.success == true) {
                        val syncedIds = unsyncedCalls.map { it.id }
                        callDao.markAsSynced(syncedIds)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // After synchronous sync, reload dashboard
        loadDashboard()
    }

    fun logManualCallSession(phoneNumber: String, durationSeconds: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val employeeId = sessionManager.getUserId().firstOrNull() ?: "UNKNOWN_EMP"
                val date = System.currentTimeMillis()
                val normalizedDbNumber = phoneNumber.replace(Regex("[^0-9+]"), "")

                val rawFingerprint = "${employeeId}${normalizedDbNumber}${date}${durationSeconds}"
                val digest = java.security.MessageDigest.getInstance("SHA-256")
                val hashBytes = digest.digest(rawFingerprint.toByteArray(Charsets.UTF_8))
                val fingerprint = hashBytes.joinToString("") { "%02x".format(it) }

                val id = "${hashPhoneNumber(phoneNumber)}_${date}"
                val matchedCall = CallEntity(
                    id = id,
                    callFingerprint = fingerprint,
                    clientPhoneHash = hashPhoneNumber(phoneNumber),
                    durationSeconds = durationSeconds,
                    callType = "OUTGOING",
                    timestamp = date,
                    isSynced = false
                )

                callDao.insertCalls(listOf(matchedCall))
                
                // Immediately sync the call and load dashboard
                syncCallsNowAndLoad()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
