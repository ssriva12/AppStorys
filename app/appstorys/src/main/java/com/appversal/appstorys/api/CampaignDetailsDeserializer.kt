package com.appversal.appstorys.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

internal class CampaignResponseDeserializer : JsonDeserializer<CampaignResponse> {
    override fun deserialize(
        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
    ): CampaignResponse {
        val jsonObject = json.asJsonObject

        val userId = jsonObject.get("user_id").asString
        val campaignsJsonArray = jsonObject.getAsJsonArray("campaigns")

        val campaigns = campaignsJsonArray.map { campaignElement ->
            val campaignObject = campaignElement.asJsonObject
            val campaignType = campaignObject.get("campaign_type")?.asString ?: ""

            val detailsJson = campaignObject.get("details")

            val details: Details? = when (campaignType) {
                "FLT" -> context.deserialize(detailsJson, FloaterDetails::class.java)
                "CSAT" -> context.deserialize(detailsJson, CSATDetails::class.java)
                "WID" -> context.deserialize(detailsJson, WidgetDetails::class.java)
                "BAN" -> context.deserialize(detailsJson, BannerDetails::class.java)
                else -> null
            }

            Campaign(
                id = campaignObject.get("id").asString,
                campaignType = campaignType,
                details = details,
                position = if (campaignObject.get("position").toString().isNotEmpty()) campaignObject.get("position")?.toString() else null
            )
        }

        return CampaignResponse(userId, campaigns)
    }
}