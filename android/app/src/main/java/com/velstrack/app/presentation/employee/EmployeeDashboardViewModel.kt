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
            androidx.work.ExistingWorkPolicy.REPLACE,
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
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val startOfDay = calendar.timeInMillis

                val cursor = context.contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.DATE),
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
                            else -> "UNKNOWN"
                        }

                        if (typeStr == "UNKNOWN") continue

                        val phoneHash = hashPhoneNumber(number)
                        val id = "${phoneHash}_${date}"

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

                if (newEntities.isNotEmpty()) {
                    callDao.insertCalls(newEntities)
                }

                val unsyncedCalls = callDao.getUnsyncedCalls()
                if (unsyncedCalls.isNotEmpty()) {
                    val dtos = unsyncedCalls.map {
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
}
