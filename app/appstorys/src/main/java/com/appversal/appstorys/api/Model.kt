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

data class ReelStatusRequest(
    val user_id: String,
    val action: String,
    val reel: String
)


data class ReelActionRequest(
    val user_id: String,
    val event_type: String,
    val reel_id: String,
    val campaign_id: String,
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
    val image: String?,
    val width: Int?,
    val height: Int?,
    val link: String,
    val styling: Styling?,
    val lottie_data: String?
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

data class CSATDetails(
    val id: String,
    val title: String,
    val height: Int?,
    val width: Int?,
    val styling: CSATStyling?,
    val thankyouImage: String,
    val thankyouText: String,
    val thankyouDescription: String,
    @SerializedName("description_text") val descriptionText: String,
    @SerializedName("feedback_option") val feedbackOption: FeedbackOption,
    val campaign: String,
    val link: String
) : Details()

data class FloaterDetails(
    val id: String,
    val image: String,
    val width: Int?,
    val height: Int?,
    val link: String,
    val position: String?,
    val campaign: String
) : Details()


data class FeedbackOption(
    val option1: String,
    val option2: String,
    val option3: String
){
    fun toList(): List<String> = listOf(option1, option2, option3).filter { it.isNotBlank() }
}

data class CSATStyling(
    val delayDisplay: Int,
    val displayDelay: String,
    val csatTitleColor: String,
    val csatCtaTextColor: String,
    val csatBackgroundColor: String,
    val csatOptionTextColour: String,
    val csatOptionStrokeColor: String,
    val csatCtaBackgroundColor: String,
    val csatDescriptionTextColor: String,
    val csatSelectedOptionTextColor: String,
    val csatSelectedOptionBackgroundColor: String
)


data class CsatFeedbackPostRequest(
    val csat: String,
    val user_id : String,
    val rating: Int,
    val feedback_option: String? = null,
    val additional_comments: String = ""
)

data class ReelsDetails(
    val id: String,
    val reels: List<Reel>
) : Details()


data class Reel(
    val id: String,
    @SerializedName("button_text") val buttonText: String,
    val order: Int,
    @SerializedName("description_text") val descriptionText: String,
    val video: String,
    val likes: Int,
    val thumbnail: String,
    val link: String
)