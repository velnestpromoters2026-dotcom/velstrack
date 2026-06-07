package com.velstrack.app.data.remote.dto

interface ApiResponse<T> {
    val success: Boolean
    val message: String
    val data: T?
}
