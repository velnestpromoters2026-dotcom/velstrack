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
    val data: LoginResponse?
) : BaseResponse

data class SimpleResponse(
    override val success: Boolean,
    override val message: String
) : BaseResponse

data class AdminDashboardResponse(
    override val success: Boolean,
    override val message: String,
    val data: AdminDashboardDto?
) : BaseResponse

data class EmployeesResponse(
    override val success: Boolean,
    override val message: String,
    val data: List<EmployeeDto>?
) : BaseResponse

data class EmployeeResponse(
    override val success: Boolean,
    override val message: String,
    val data: EmployeeDto?
) : BaseResponse

data class MetaCampaignsResponse(
    override val success: Boolean,
    override val message: String,
    val data: List<MetaCampaignDto>?
) : BaseResponse

data class MetaStatusResponse(
    override val success: Boolean,
    override val message: String,
    val data: MetaStatusDto?
) : BaseResponse

data class TargetsResponse(
    override val success: Boolean,
    override val message: String,
    val data: List<TargetDto>?
) : BaseResponse

data class TargetResponse(
    override val success: Boolean,
    override val message: String,
    val data: TargetDto?
) : BaseResponse

data class AnalyticsResponse(
    override val success: Boolean,
    override val message: String,
    val data: AnalyticsDto?
) : BaseResponse

data class EmployeeDashboardResponse(
    override val success: Boolean,
    override val message: String,
    val data: EmployeeDashboardDto?
) : BaseResponse

data class CallsResponse(
    override val success: Boolean,
    override val message: String,
    val data: List<CallLogDto>?
) : BaseResponse

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

    @GET("admin/calls")
    suspend fun getCalls(): Response<CallsResponse>
}
