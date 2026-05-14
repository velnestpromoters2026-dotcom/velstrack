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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class EmployeeDashboardViewModel @Inject constructor(
    private val repository: EmployeeRepository,
    private val workManager: WorkManager
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
    }
}
