package com.velstrack.app.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import com.velstrack.app.data.remote.dto.*

data class LoginResponse(
    val _id: String,
    val email: String,
    val role: String,
    val token: String
)

interface ApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: Map<String, String>): Response<ApiResponse<LoginResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @POST("calls/sync")
    suspend fun syncCalls(@Body request: SyncCallRequest): Response<ApiResponse<Any>>

    @GET("admin/dashboard")
    suspend fun getAdminDashboardStats(): Response<ApiResponse<AdminDashboardDto>>

    @GET("admin/employees")
    suspend fun getEmployees(): Response<ApiResponse<List<EmployeeDto>>>

    @GET("admin/employees/{id}")
    suspend fun getEmployee(@Path("id") id: String): Response<ApiResponse<EmployeeDto>>

    @POST("admin/employees")
    suspend fun addEmployee(@Body request: AddEmployeeRequest): Response<ApiResponse<EmployeeDto>>

    @GET("meta/campaigns")
    suspend fun getMetaCampaigns(): Response<ApiResponse<List<MetaCampaignDto>>>

    @GET("admin/meta/status")
    suspend fun getMetaStatus(): Response<ApiResponse<MetaStatusDto>>

    @GET("admin/targets")
    suspend fun getTargets(): Response<ApiResponse<List<TargetDto>>>

    @POST("admin/targets")
    suspend fun createTarget(@Body request: CreateTargetRequest): Response<ApiResponse<TargetDto>>

    @GET("admin/analytics")
    suspend fun getAnalytics(): Response<ApiResponse<AnalyticsDto>>

    @GET("employee/dashboard")
    suspend fun getEmployeeDashboardStats(): Response<ApiResponse<EmployeeDashboardDto>>
}
