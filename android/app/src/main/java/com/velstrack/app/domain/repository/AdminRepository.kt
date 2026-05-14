package com.velstrack.app.domain.repository

import com.velstrack.app.data.remote.api.ApiService
import com.velstrack.app.data.remote.dto.AddEmployeeRequest
import com.velstrack.app.data.remote.dto.AdminDashboardDto
import com.velstrack.app.data.remote.dto.EmployeeDto
import com.velstrack.app.data.remote.dto.MetaCampaignDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.velstrack.app.core.util.safeApiCall
import javax.inject.Inject

class AdminRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getDashboardStats(): Flow<Result<AdminDashboardDto>> = flow {
        emit(safeApiCall { apiService.getAdminDashboardStats() })
    }

    fun getEmployees(): Flow<Result<List<EmployeeDto>>> = flow {
        emit(safeApiCall { apiService.getEmployees() })
    }

    fun addEmployee(request: AddEmployeeRequest): Flow<Result<EmployeeDto>> = flow {
        emit(safeApiCall { apiService.addEmployee(request) })
    }

    fun getMetaCampaigns(): Flow<Result<List<MetaCampaignDto>>> = flow {
        emit(safeApiCall { apiService.getMetaCampaigns() })
    }
}
