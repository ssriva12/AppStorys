package com.example.carousal.utils.campaign

import com.example.carousal.utils.AppStorysService
import com.example.carousal.utils.types.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

data class CampaignState(
    val loading: Boolean = false,
    val error: String? = null,
    val data: CampaignData = CampaignData(),
    val initialized: Boolean = false
)

data class CampaignData(
    val campaigns: List<Any> = emptyList(),
    val accessToken: String = "",
    val userId: String = ""
)

@Singleton
class CampaignService @Inject constructor(
    private val appStorysService: AppStorysService
) {
    private val _state = MutableStateFlow(CampaignState())
    val state: StateFlow<CampaignState> = _state.asStateFlow()

    val loading: StateFlow<Boolean> = MutableStateFlow(_state.value.loading)
    val error: StateFlow<String?> = MutableStateFlow(_state.value.error)
    val campaignData: StateFlow<CampaignData> = MutableStateFlow(_state.value.data)

    suspend fun initializeCampaigns(
        accountId: String,
        appId: String,
        userId: String,
        screenName: String
    ) {
        if (_state.value.loading) return

        try {
            _state.update { currentState ->
                currentState.copy(
                    loading = true,
                    error = null,
                    initialized = true
                )
            }

            // First verify account and get tokens
            appStorysService.verifyAccount(accountId, appId)

            // Get access token
            val accessToken = appStorysService.getAccessToken()
                ?: throw Exception("Access token not found")

            // Get screen campaigns
            val screenData = appStorysService.trackScreen(appId, screenName)
                ?: throw Exception("No campaigns available")

            // Verify user and get campaign details
            val userData = appStorysService.verifyUser(userId, screenData)
                ?: throw Exception("Failed to get user campaign data")

            // Update state with new campaign data
            _state.update { currentState ->
                currentState.copy(
                    loading = false,
                    error = null,
                    data = CampaignData(
                        campaigns = (userData as? UserData)?.campaigns ?: emptyList(),
                        accessToken = accessToken,
                        userId = userId
                    ),
                    initialized = true
                )
            }

        } catch (error: Exception) {
            _state.update { currentState ->
                currentState.copy(
                    loading = false,
                    error = error.message ?: "An unknown error occurred",
                    initialized = true
                )
            }
            println("Error initializing campaigns: ${error.message}")
        }
    }
} 