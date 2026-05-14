package com.velstrack.app.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velstrack.app.core.util.UiState
import com.velstrack.app.data.remote.dto.AdminDashboardDto
import com.velstrack.app.data.remote.dto.EmployeeDto
import com.velstrack.app.data.remote.dto.MetaCampaignDto
import com.velstrack.app.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<UiState<AdminDashboardDto>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<AdminDashboardDto>> = _dashboardState

    private val _employeesState = MutableStateFlow<UiState<List<EmployeeDto>>>(UiState.Loading)
    val employeesState: StateFlow<UiState<List<EmployeeDto>>> = _employeesState

    private val _metaState = MutableStateFlow<UiState<List<MetaCampaignDto>>>(UiState.Loading)
    val metaState: StateFlow<UiState<List<MetaCampaignDto>>> = _metaState

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

    fun loadEmployees() {
        _employeesState.value = UiState.Loading
        viewModelScope.launch {
            repository.getEmployees().collect { result ->
                if (result.isSuccess) {
                    val list = result.getOrNull()!!
                    if (list.isEmpty()) {
                        _employeesState.value = UiState.Empty
                    } else {
                        _employeesState.value = UiState.Success(list)
                    }
                } else {
                    _employeesState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    fun loadMetaCampaigns() {
        _metaState.value = UiState.Loading
        viewModelScope.launch {
            repository.getMetaCampaigns().collect { result ->
                if (result.isSuccess) {
                    val list = result.getOrNull()!!
                    if (list.isEmpty()) {
                        _metaState.value = UiState.Empty
                    } else {
                        _metaState.value = UiState.Success(list)
                    }
                } else {
                    _metaState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}
