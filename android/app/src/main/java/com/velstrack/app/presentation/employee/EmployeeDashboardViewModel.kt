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
import android.provider.ContactsContract
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.net.Uri
import com.velstrack.app.data.local.dao.CallDao
import com.velstrack.app.data.local.entity.TrackedCallSession
import com.velstrack.app.data.remote.api.ApiService
import com.velstrack.app.core.datastore.SessionManager
import com.velstrack.app.data.remote.dto.SyncCallDto
import com.velstrack.app.data.remote.dto.SyncCallRequest
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

import com.velstrack.app.domain.export.ExcelExportManager
import com.velstrack.app.domain.updater.AppUpdater
import java.util.UUID

@HiltViewModel
class EmployeeDashboardViewModel @Inject constructor(
    private val repository: EmployeeRepository,
    private val workManager: WorkManager,
    private val callDao: CallDao,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val excelExportManager: ExcelExportManager,
    private val appUpdater: AppUpdater,
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

        val immediateRequest = androidx.work.OneTimeWorkRequestBuilder<SyncCallWorker>().build()
        workManager.enqueueUniqueWork(
            "CallSyncWorker_Immediate",
            androidx.work.ExistingWorkPolicy.KEEP,
            immediateRequest
        )
    }

    fun createPendingSession(phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val employeeId = sessionManager.getUserId().firstOrNull() ?: "UNKNOWN"
            val session = TrackedCallSession(
                sessionId = UUID.randomUUID().toString(),
                employeeId = employeeId,
                phoneNumber = phoneNumber,
                startedAt = System.currentTimeMillis(),
                status = "PENDING"
            )
            callDao.insertTrackedCallSession(session)
        }
    }

    private fun getContactName(phoneNumber: String): String {
        var contactName = "Unknown Contact"
        try {
            val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    contactName = it.getString(0) ?: "Unknown Contact"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return contactName
    }

    private fun formatDuration(seconds: Int): String {
        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s"
            else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
        }
    }

    suspend fun syncCallsNowAndLoad() {
        _dashboardState.value = UiState.Loading
        withContext(Dispatchers.IO) {
            try {
                // 1. Verify pending sessions
                val pendingSessions = callDao.getPendingSessions()
                if (pendingSessions.isNotEmpty()) {
                    kotlinx.coroutines.delay(2000) // Give OS time to write to CallLog

                    pendingSessions.forEach { session ->
                        val searchTime = session.startedAt - 60000 // 1 min buffer
                        val cursor = context.contentResolver.query(
                            CallLog.Calls.CONTENT_URI,
                            arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.DATE),
                            "${CallLog.Calls.DATE} >= ?",
                            arrayOf(searchTime.toString()),
                            "${CallLog.Calls.DATE} DESC"
                        )

                        var matched = false
                        cursor?.use { c ->
                            val numberIdx = c.getColumnIndex(CallLog.Calls.NUMBER)
                            val durationIdx = c.getColumnIndex(CallLog.Calls.DURATION)
                            val typeIdx = c.getColumnIndex(CallLog.Calls.TYPE)
                            val dateIdx = c.getColumnIndex(CallLog.Calls.DATE)

                            while (c.moveToNext() && !matched) {
                                val number = c.getString(numberIdx) ?: continue
                                val normalizedDbNumber = number.replace(Regex("[^0-9+]"), "")
                                val normalizedPending = session.phoneNumber.replace(Regex("[^0-9+]"), "")
                                
                                if (normalizedDbNumber == normalizedPending || normalizedDbNumber.contains(normalizedPending) || normalizedPending.contains(normalizedDbNumber)) {
                                    val duration = c.getInt(durationIdx)
                                    val typeInt = c.getInt(typeIdx)
                                    val date = c.getLong(dateIdx)

                                    val typeStr = when (typeInt) {
                                        CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                                        else -> "UNKNOWN"
                                    }

                                    if (typeStr == "OUTGOING") {
                                        // VERIFIED COMPANY CALL
                                        val rawFingerprint = "${session.employeeId}${normalizedDbNumber}${date}${duration}"
                                        val digest = MessageDigest.getInstance("SHA-256")
                                        val hashBytes = digest.digest(rawFingerprint.toByteArray(Charsets.UTF_8))
                                        val fingerprint = hashBytes.joinToString("") { "%02x".format(it) }

                                        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                                        val verifiedSession = session.copy(
                                            status = "VERIFIED",
                                            contactName = getContactName(number),
                                            callDate = dateFormat.format(Date(date)),
                                            callTime = timeFormat.format(Date(date)),
                                            durationSeconds = duration,
                                            callType = typeStr,
                                            callFingerprint = fingerprint
                                        )
                                        
                                        callDao.insertTrackedCallSession(verifiedSession)
                                        matched = true
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Sync verified sessions
                val unsyncedSessions = callDao.getUnsyncedSessions()
                if (unsyncedSessions.isNotEmpty()) {
                    val dtos = unsyncedSessions.map {
                        SyncCallDto(
                            clientPhoneHash = it.phoneNumber, // Server expects unhashed phone now per latest changes
                            durationSeconds = it.durationSeconds ?: 0,
                            callType = it.callType ?: "OUTGOING",
                            timestamp = it.startedAt,
                            callFingerprint = it.callFingerprint ?: "NO_FINGERPRINT",
                            isVelstrackCall = true
                        )
                    }

                    val request = SyncCallRequest(calls = dtos)
                    val response = apiService.syncCalls(request)

                    if (response.isSuccessful && response.body()?.success == true) {
                        val syncedIds = unsyncedSessions.map { it.sessionId }
                        callDao.markSessionsAsSynced(syncedIds)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        loadDashboard()
    }

    suspend fun exportCallsToExcel(): String? {
        return excelExportManager.exportCallsToExcel()
    }

    fun checkForUpdates(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = appUpdater.checkForUpdates()
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
}
