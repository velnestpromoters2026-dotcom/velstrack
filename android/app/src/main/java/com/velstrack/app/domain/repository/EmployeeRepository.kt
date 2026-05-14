package com.velstrack.app.domain.repository

import com.velstrack.app.data.remote.api.ApiService
import com.velstrack.app.data.remote.dto.EmployeeDashboardDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.velstrack.app.core.util.safeApiCall
import javax.inject.Inject

class EmployeeRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getDashboardStats(): Flow<Result<EmployeeDashboardDto>> = flow {
        emit(safeApiCall { apiService.getEmployeeDashboardStats() })
    }
}
