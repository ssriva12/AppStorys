package com.example.carousal.utils

import com.example.carousal.utils.types.ActionType
import javax.inject.Inject

class AppStorysService @Inject constructor(
    private val trackScreenService: TrackScreenService,
    private val trackUserActionService: TrackUserActionService,
    private val verifyAccountService: VerifyAccountService,
    private val verifyUserService: VerifyUserService,
    private val storageService: StorageService
) {
    suspend fun getAccessToken(): String? {
        return storageService.getItem("access_token")
    }

    suspend fun trackScreen(appId: String, screenName: String): Any? {
        return trackScreenService.trackScreen(appId, screenName)
    }

    suspend fun trackUserAction(userId: String, campaignId: String, action: ActionType) {
        trackUserActionService.trackUserAction(userId, campaignId, action)
    }

    suspend fun verifyAccount(accountId: String, appId: String) {
        verifyAccountService.verifyAccount(accountId, appId)
    }

    suspend fun verifyUser(userId: String, campaigns: Any): Any? {
        return verifyUserService.verifyUser(userId, campaigns)
    }
} 