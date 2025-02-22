package com.appversal.appstorys

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiRepository(private val apiService: ApiService) {

    suspend fun getAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            val response = apiService.validateAccount(
                ValidateAccountRequest(
                    app_id = "afadf960-3975-4ba2-933b-fac71ccc2002",
                    account_id = "13555479-077f-445e-87f0-e6eae2e215c5"
                )
            )
            response.access_token
        }
    }

    suspend fun getCampaigns(accessToken: String): List<String> {
        return withContext(Dispatchers.IO) {
            val response = apiService.trackScreen(
                token = "Bearer $accessToken",
                request = TrackScreenRequest(screen_name = "Home Screen")
            )
            response.campaigns
        }
    }

    suspend fun getCampaignData(accessToken: String, campaignList: List<String>): CampaignResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.trackUser(
                token = "Bearer $accessToken",
                request = TrackUserRequest(user_id = "krishna", campaign_list = campaignList)
            )
            response
        }
    }
}