package com.appversal.appstorys

import com.google.gson.annotations.JsonAdapter

data class ValidateAccountRequest(
    val app_id: String,
    val account_id: String
)

data class ValidateAccountResponse(
    val access_token: String
)

data class TrackScreenRequest(
    val screen_name: String
)

data class TrackScreenResponse(
    val campaigns: List<String>
)

data class TrackUserRequest(
    val user_id: String,
    val campaign_list: List<String>
)
//Campaign Data

data class CampaignResponse(
    val user_id: String,
    val campaigns: List<Campaign>
)

data class Campaign(
    val id: String,
    val campaign_type: String,
    @JsonAdapter(CampaignDetailsDeserializer::class)
    val details: Any?, // Can be List<CampaignDetail> or CampaignDetail
)

data class CampaignDetail(
    val id: String,
    val name: String,
    val image: String?,
    val ringColor: String?,
    val nameColor: String?,
    val order: Int?,
    val slides: List<Slide>?
)

data class Banner(
    val height: Int,
    val id: String,
    val image: String,
    val link: String,
    val styling: Styling,
    val width: Any
)

data class Slide(
    val id: String,
    val parent: String,
    val image: String?,
    val video: String?,
    val link: String?,
    val button_text: String?,
    val order: Int
)

data class SurveyDetail(
    val id: String,
    val name: String,
    val styling: SurveyStyling?
)

data class SurveyStyling(
    val optionColor: String?,
    val backgroundColor: String?,
    val optionTextColor: String?
)
