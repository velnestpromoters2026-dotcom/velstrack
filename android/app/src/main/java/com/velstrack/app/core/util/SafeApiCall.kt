package com.velstrack.app.core.util

import com.velstrack.app.data.remote.dto.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Unknown API Error"))
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Unauthorized Access"
                    403 -> "Forbidden"
                    404 -> "Resource Not Found"
                    500 -> "Internal Server Error"
                    else -> "HTTP Error: ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network Error: Could not connect to server"))
        }
    }
}
