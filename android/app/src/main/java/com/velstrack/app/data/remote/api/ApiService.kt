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

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    override val success: Boolean,
    override val message: String,
    override val data: LoginResponse?
) : ApiResponse<LoginResponse>

data class SimpleResponse(
    override val success: Boolean,
    override val message: String,
    override val data: Unit? = null
) : ApiResponse<Unit>

data class AdminDashboardResponse(
    override val success: Boolean,
    override val message: String,
    override val data: AdminDashboardDto?
) : ApiResponse<AdminDashboardDto>

data class EmployeesResponse(
    override val success: Boolean,
    override val message: String,
    override val data: List<EmployeeDto>?
) : ApiResponse<List<EmployeeDto>>

data class EmployeeResponse(
    override val success: Boolean,
    override val message: String,
    override val data: EmployeeDto?
) : ApiResponse<EmployeeDto>

data class MetaCampaignsResponse(
    override val success: Boolean,
    override val message: String,
    override val data: List<MetaCampaignDto>?
) : ApiResponse<List<MetaCampaignDto>>

data class MetaStatusResponse(
    override val success: Boolean,
    override val message: String,
    override val data: MetaStatusDto?
) : ApiResponse<MetaStatusDto>

data class TargetsResponse(
    override val success: Boolean,
    override val message: String,
    override val data: List<TargetDto>?
) : ApiResponse<List<TargetDto>>

data class TargetResponse(
    override val success: Boolean,
    override val message: String,
    override val data: TargetDto?
) : ApiResponse<TargetDto>

data class AnalyticsResponse(
    override val success: Boolean,
    override val message: String,
    override val data: AnalyticsDto?
) : ApiResponse<AnalyticsDto>

data class EmployeeDashboardResponse(
    override val success: Boolean,
    override val message: String,
    override val data: EmployeeDashboardDto?
) : ApiResponse<EmployeeDashboardDto>

interface ApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<SimpleResponse>

    @POST("calls/sync")
    suspend fun syncCalls(@Body request: SyncCallRequest): Response<SimpleResponse>

    @GET("admin/dashboard")
    suspend fun getAdminDashboardStats(): Response<AdminDashboardResponse>

    @GET("admin/employees")
    suspend fun getEmployees(): Response<EmployeesResponse>

    @GET("admin/employees/{id}")
    suspend fun getEmployee(@Path("id") id: String): Response<EmployeeResponse>

    @POST("admin/employees")
    suspend fun addEmployee(@Body request: AddEmployeeRequest): Response<EmployeeResponse>

    @GET("meta/campaigns")
    suspend fun getMetaCampaigns(): Response<MetaCampaignsResponse>

    @GET("admin/meta/status")
    suspend fun getMetaStatus(): Response<MetaStatusResponse>

    @GET("admin/targets")
    suspend fun getTargets(): Response<TargetsResponse>

    @POST("admin/targets")
    suspend fun createTarget(@Body request: CreateTargetRequest): Response<TargetResponse>

    @GET("admin/analytics")
    suspend fun getAnalytics(): Response<AnalyticsResponse>

    @GET("employee/dashboard")
    suspend fun getEmployeeDashboardStats(): Response<EmployeeDashboardResponse>
}
