package com.example.carousal.utils

import com.example.carousal.utils.config.ApiConfig
import com.example.carousal.utils.types.UserData
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Headers
import javax.inject.Inject

interface VerifyUserApi {
    @POST(ApiConfig.Endpoints.TRACK_USER)
    @Headers("Content-Type: application/json")
    suspend fun verifyUser(
        @Body body: VerifyUserRequest
    ): VerifyUserResponse
}

data class VerifyUserRequest(
    val user_id: String,
    val app_id: String,
    val campaign_list: List<Any>,
    val attributes: Map<String, Any>? = null
)

data class VerifyUserResponse(
    val campaigns: List<Any>
)

class VerifyUserService @Inject constructor(
    private val api: VerifyUserApi,
    private val storageService: StorageService,
    private val errorHandler: ErrorHandlerService
) {
    suspend fun verifyUser(userId: String, campaigns: Any): UserData? {
        try {
            val campaignList = (campaigns as? Map<*, *>)?.get("campaigns") as? List<*>
                ?: return null

            if (campaignList.isEmpty()) {
                println("No campaigns found")
                return null
            }

            val appId = storageService.getItem("app_id")
            val accessToken = storageService.getItem("access_token")

            if (appId == null || accessToken == null) {
                throw Exception("Missing required authentication data")
            }

            val response = api.verifyUser(
                VerifyUserRequest(
                    user_id = userId,
                    app_id = appId,
                    campaign_list = campaignList as List<Any>
                )
            )

            return UserData(
                campaigns = response.campaigns.map { it as Campaign },
                userId = userId
            )
        } catch (e: Exception) {
            println("Error in verifyUser: ${e.message}")
            throw e
        }
    }
} 