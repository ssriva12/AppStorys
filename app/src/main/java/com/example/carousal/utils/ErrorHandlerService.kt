package com.example.carousal.utils

import retrofit2.HttpException
import java.io.IOException

data class ApiError(
    val status: Int,
    val message: String,
    val details: Any? = null
)

class ErrorHandlerService {
    fun handleError(error: Throwable): ApiError {
        return when (error) {
            is HttpException -> {
                ApiError(
                    status = error.code(),
                    message = error.message(),
                    details = error.response()?.errorBody()
                )
            }
            is IOException -> {
                ApiError(
                    status = 503,
                    message = "Network error occurred",
                    details = error.message
                )
            }
            else -> {
                ApiError(
                    status = 500,
                    message = "An unknown error occurred",
                    details = error.message
                )
            }
        }
    }
} 