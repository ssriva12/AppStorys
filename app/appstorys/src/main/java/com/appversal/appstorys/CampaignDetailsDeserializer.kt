package com.appversal.appstorys

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CampaignDetailsDeserializer : JsonDeserializer<Any> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): Any? {
        return when {
            json.isJsonArray -> context.deserialize<List<CampaignDetail>>(
                json,
                object : TypeToken<List<CampaignDetail>>() {}.type
            )
            json.isJsonObject -> context.deserialize<CampaignDetail>(json, CampaignDetail::class.java)
            else -> throw JsonParseException("Unexpected JSON type for Campaign.details")
        }
    }
}