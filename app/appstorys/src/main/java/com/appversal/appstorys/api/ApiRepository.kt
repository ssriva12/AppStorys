package com.appversal.appstorys.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class ApiRepository(private val apiService: ApiService) {

    suspend fun getAccessToken(app_id: String, account_id: String): String? {
        return withContext(Dispatchers.IO) {
            val response = apiService.validateAccount(
                ValidateAccountRequest(
                    app_id = app_id,
                    account_id = account_id
                )
            )
            response.access_token
        }
    }

    suspend fun getCampaigns(accessToken: String, screenName: String, positions: List<String>?): List<String> {
        return withContext(Dispatchers.IO) {
            val response = apiService.trackScreen(
                token = "Bearer $accessToken",
                request = TrackScreenRequest(screen_name = screenName, positions)
            )
            response.campaigns
        }
    }

    suspend fun getCampaignData(accessToken: String, userId: String, campaignList: List<String>, attributes: List<Map<String, Any>>?): CampaignResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.trackUser(
                token = "Bearer $accessToken",
                request = TrackUserRequest(user_id = userId, campaign_list = campaignList, attributes = attributes)
            )
            response
        }
    }


    suspend fun trackActions(accessToken: String, actions: TrackAction){
        withContext(Dispatchers.IO) {
            apiService.trackAction(
                token = "Bearer $accessToken",
                request = actions
            )
        }
    }

    suspend fun captureCSATResponse(accessToken: String, actions: CsatFeedbackPostRequest){
        withContext(Dispatchers.IO) {
            apiService.sendCSATResponse(
                token = "Bearer $accessToken",
                request = actions
            )
        }
    }
}