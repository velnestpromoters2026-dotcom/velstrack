package com.velstrack.app.presentation.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velstrack.app.core.util.UiState
import com.velstrack.app.data.remote.dto.EmployeeDashboardDto
import com.velstrack.app.domain.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeDashboardViewModel @Inject constructor(
    private val repository: EmployeeRepository
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
}
