package com.example.parkingapi.exception

data class ApiResponse<T>(
    val message: String,
    val response: T? = null,
)