package com.example.carousal.utils

import com.example.carousal.utils.config.ApiConfig
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Headers
import javax.inject.Inject

interface TrackScreenApi {
    @POST(ApiConfig.Endpoints.TRACK_SCREEN)
    @Headers("Content-Type: application/json")
    suspend fun trackScreen(
        @Body body: TrackScreenRequest
    ): List<Any>
}

data class TrackScreenRequest(
    val app_id: String,
    val screen_name: String
)

class TrackScreenService @Inject constructor(
    private val api: TrackScreenApi,
    private val storageService: StorageService
) {
    suspend fun trackScreen(appId: String, screenName: String): List<Any>? {
        return try {
            val accessToken = storageService.getItem("access_token")
            if (accessToken == null) {
                println("Access token not found")
                return null
            }

            api.trackScreen(TrackScreenRequest(appId, screenName))
        } catch (e: Exception) {
            println("Error in trackScreen: ${e.message}")
            null
        }
    }
} 