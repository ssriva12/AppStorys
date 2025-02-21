package com.appversal.appstorys.types

sealed class Campaign {
    data class Floater(
        val id: String,
        val campaignType: String = "FLT",
        val details: FloaterDetails
    ) : Campaign()

    data class Banner(
        val id: String,
        val campaignType: String = "BAN",
        val details: BannerDetails
    ) : Campaign()
}

data class FloaterDetails(
    val id: String,
    val image: String,
    val link: String?
)

data class BannerDetails(
    val id: String,
    val image: String,
    val width: Int?,
    val height: Int?,
    val link: String?
)

data class UserData(
    val campaigns: List<Campaign>,
    val userId: String
)

enum class ActionType {
    CLICK,
    VIEW,
    DISMISS,
    STORY_VIEW
} 