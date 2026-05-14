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
                var errorMsg = "HTTP Error: ${response.code()}"
                try {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null && errorBodyString.contains("message")) {
                        val startIndex = errorBodyString.indexOf("\"message\":\"") + 11
                        val endIndex = errorBodyString.indexOf("\"", startIndex)
                        if (startIndex > 10 && endIndex > startIndex) {
                            errorMsg = errorBodyString.substring(startIndex, endIndex)
                        }
                    } else if (response.code() == 500) {
                        errorMsg = "Internal Server Error"
                    }
                } catch (e: Exception) {
                    if (response.code() == 500) errorMsg = "Internal Server Error"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network Error: Could not connect to server"))
        }
    }
}
