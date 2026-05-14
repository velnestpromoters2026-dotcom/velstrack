package com.velstrack.app.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: Map<String, String>): Response<Any> // Replace Any with DTO

    @POST("calls/sync")
    suspend fun syncCalls(@Body request: Map<String, Any>): Response<Any>

    @GET("admin/dashboard")
    suspend fun getDashboardStats(): Response<Any>
}
