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
        emit(safeApiCall(
            apiCall = { apiService.getAdminDashboardStats() },
            extractData = { it.data }
        ))
    }

    fun getEmployees(): Flow<Result<List<EmployeeDto>>> = flow {
        emit(safeApiCall(
            apiCall = { apiService.getEmployees() },
            extractData = { it.data }
        ))
    }

    fun addEmployee(request: AddEmployeeRequest): Flow<Result<EmployeeDto>> = flow {
        emit(safeApiCall(
            apiCall = { apiService.addEmployee(request) },
            extractData = { it.data }
        ))
    }

    fun getMetaCampaigns(): Flow<Result<List<MetaCampaignDto>>> = flow {
        emit(safeApiCall(
            apiCall = { apiService.getMetaCampaigns() },
            extractData = { it.data }
        ))
    }

    fun getMetaStatus(): Flow<Result<MetaStatusDto>> = flow {
        emit(safeApiCall(
            apiCall = { apiService.getMetaStatus() },
            extractData = { it.data }
        ))
    }

    fun getTargets(): Flow<Result<List<TargetDto>>> = flow {
        emit(safeApiCall(
            apiCall = { apiService.getTargets() },
            extractData = { it.data }
        ))
    }

    fun createTarget(request: CreateTargetRequest): Flow<Result<TargetDto>> = flow {
        emit(safeApiCall(
            apiCall = { apiService.createTarget(request) },
            extractData = { it.data }
        ))
    }

    fun getAnalytics(): Flow<Result<AnalyticsDto>> = flow {
        emit(safeApiCall(
            apiCall = { apiService.getAnalytics() },
            extractData = { it.data }
        ))
    }
}
