package com.velstrack.app.data.remote.dto

data class EmployeeDto(
    val _id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String,
    val isOnline: Boolean?,
    val lastActive: String?,
    val department: String?
)

data class AddEmployeeRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String,
    val department: String
)

data class AdminDashboardDto(
    val totalEmployees: Int?,
    val activeEmployees: Int?,
    val totalCallsSynced: Int?,
    val recentActivity: List<ActivityDto>?
)

data class ActivityDto(
    val _id: String,
    val description: String,
    val timestamp: String,
    val type: String
)

data class MetaCampaignDto(
    val _id: String,
    val campaignName: String,
    val status: String,
    val spend: Double,
    val reach: Int,
    val clicks: Int,
    val ctr: Double,
    val cpc: Double,
    val trend: String?
)

data class EmployeeDashboardDto(
    val callsToday: Int?,
    val target: Int?,
    val callsTrend: String?,
    val recentCalls: List<CallActivityDto>?
)

data class CallActivityDto(
    val _id: String,
    val contactName: String,
    val duration: String,
    val timestamp: String
)
