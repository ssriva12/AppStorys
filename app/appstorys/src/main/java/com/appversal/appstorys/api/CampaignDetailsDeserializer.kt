package com.appversal.appstorys.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

internal class CampaignDetailsDeserializer : JsonDeserializer<Details> {

    override fun deserialize(
        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
    ): Details? {
        try {
            val jsonObject = json.asJsonObject

            return if (jsonObject.has("widget_images")) {
                context.deserialize(json, WidgetDetails::class.java)
            } else {
                context.deserialize(json, BannerDetails::class.java)
            }
        }catch (e: Exception){

        }
        return null
    }
}

