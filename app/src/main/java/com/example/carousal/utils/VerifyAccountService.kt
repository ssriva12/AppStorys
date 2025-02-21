package com.example.carousal.utils

import com.example.carousal.utils.config.ApiConfig
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Headers
import javax.inject.Inject

interface VerifyAccountApi {
    @POST(ApiConfig.Endpoints.VALIDATE_ACCOUNT)
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    suspend fun verifyAccount(
        @Body body: VerifyAccountRequest
    ): VerifyAccountResponse
}

data class VerifyAccountRequest(
    val account_id: String,
    val app_id: String
)

data class VerifyAccountResponse(
    val access_token: String,
    val refresh_token: String
)

class VerifyAccountService @Inject constructor(
    private val api: VerifyAccountApi,
    private val storageService: StorageService
) {
    suspend fun verifyAccount(accountId: String, appId: String) {
        try {
            storageService.setItem("app_id", appId)

            val response = api.verifyAccount(
                VerifyAccountRequest(
                    account_id = accountId,
                    app_id = appId
                )
            )

            storageService.setItem("access_token", response.access_token)
            storageService.setItem("refresh_token", response.refresh_token)
        } catch (e: Exception) {
            println("Error in verifyAccount: ${e.message}")
        }
    }
} 