package com.appversal.appstorys.api

import com.google.gson.annotations.SerializedName

data class ValidateAccountRequest(
    val app_id: String,
    val account_id: String
)

data class TrackAction(
    val campaign_id: String,
    val user_id: String,
    val event_type: String,
    val widget_image: String?
)


data class ValidateAccountResponse(
    val access_token: String
)

data class TrackScreenRequest(
    val screen_name: String,
    val position_list: List<String>?
)

data class TrackScreenResponse(
    val campaigns: List<String>
)

data class TrackUserRequest(
    val user_id: String,
    val campaign_list: List<String>,
     val attributes: List<Map<String, Any>>?
)
//Campaign Data

data class CampaignResponse(
    val user_id: String,
    val campaigns: List<Campaign>
)

data class Campaign(
    val id: String,
    @SerializedName("campaign_type") val campaignType: String,
    val details: Details?,
    val position: String?
)

sealed class Details

data class BannerDetails(
    val id: String,
    val image: String,
    val width: Int?,
    val height: Int?,
    val link: String,
    val styling: Styling?
) : Details()

data class Styling(
    val isClose: Boolean,
    val marginBottom: Int,
    val topLeftBorderRadius: Int,
    val topRightBorderRadius: Int,
    val bottomLeftBorderRadius: Int,
    val bottomRightBorderRadius: Int
)

data class WidgetDetails(
    val id: String,
    val type: String,
    val width: Int?,
    val height: Int?,
    @SerializedName("widget_images") val widgetImages: List<WidgetImage>,
    val campaign: String,
    val screen: String,
    val styling: Styling?
) : Details()

data class WidgetImage(
    val id: String,
    val image: String,
    val link: String,
    val order: Int
)