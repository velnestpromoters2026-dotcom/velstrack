package com.velstrack.app.domain.repository

import com.velstrack.app.data.remote.api.ApiService
import com.velstrack.app.data.remote.dto.AddEmployeeRequest
import com.velstrack.app.data.remote.dto.AdminDashboardDto
import com.velstrack.app.data.remote.dto.EmployeeDto
import com.velstrack.app.data.remote.dto.MetaCampaignDto
import com.velstrack.app.data.remote.dto.MetaStatusDto
import com.velstrack.app.data.remote.dto.TargetDto
import com.velstrack.app.data.remote.dto.CreateTargetRequest
import com.velstrack.app.data.remote.dto.AnalyticsDto
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

    fun getMetaStatus(): Flow<Result<MetaStatusDto>> = flow {
        emit(safeApiCall { apiService.getMetaStatus() })
    }

    fun getTargets(): Flow<Result<List<TargetDto>>> = flow {
        emit(safeApiCall { apiService.getTargets() })
    }

    fun createTarget(request: CreateTargetRequest): Flow<Result<TargetDto>> = flow {
        emit(safeApiCall { apiService.createTarget(request) })
    }

    fun getAnalytics(): Flow<Result<AnalyticsDto>> = flow {
        emit(safeApiCall { apiService.getAnalytics() })
    }
}
