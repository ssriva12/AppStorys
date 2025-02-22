package com.appversal.appstorys

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://backend.appstorys.com/"

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Details::class.java, CampaignDetailsDeserializer()) // Register custom deserializer
        .create()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Use the custom Gson instance
            .build()
            .create(ApiService::class.java)
    }
}
