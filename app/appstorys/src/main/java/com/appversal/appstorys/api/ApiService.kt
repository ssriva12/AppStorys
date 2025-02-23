package com.appversal.appstorys.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/v1/users/validate-account/")
    suspend fun validateAccount(
        @Body request: ValidateAccountRequest
    ): ValidateAccountResponse

    @POST("api/v1/users/track-screen/")
    suspend fun trackScreen(
        @Header("Authorization") token: String,
        @Body request: TrackScreenRequest
    ): TrackScreenResponse

    @POST("api/v1/users/track-user/")
    suspend fun trackUser(
        @Header("Authorization") token: String,
        @Body request: TrackUserRequest,

    ): CampaignResponse

    @POST("api/v1/users/track-action/")
    suspend fun trackAction(
        @Header("Authorization") token: String,
        @Body request: TrackAction
    )
}
