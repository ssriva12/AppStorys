package com.example.carousal.utils

import com.example.carousal.utils.config.ApiConfig
import com.example.carousal.utils.types.ActionType
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Headers
import javax.inject.Inject

interface TrackUserActionApi {
    @POST(ApiConfig.Endpoints.TRACK_ACTION)
    @Headers("Content-Type: application/json")
    suspend fun trackAction(
        @Body body: TrackUserActionRequest
    )
}

data class TrackUserActionRequest(
    val campaign_id: String,
    val user_id: String,
    val event_type: ActionType,
    val story_slide: String? = null
)

class TrackUserActionService @Inject constructor(
    private val api: TrackUserActionApi,
    private val storageService: StorageService
) {
    suspend fun trackUserAction(
        userId: String,
        campaignId: String,
        eventType: ActionType,
        storySlide: String? = null
    ) {
        try {
            val accessToken = storageService.getItem("access_token")
            if (accessToken == null) {
                throw Exception("Access token not found")
            }

            api.trackAction(
                TrackUserActionRequest(
                    campaign_id = campaignId,
                    user_id = userId,
                    event_type = eventType,
                    story_slide = storySlide
                )
            )
        } catch (e: Exception) {
            println("Error in trackUserAction: ${e.message}")
        }
    }
} 