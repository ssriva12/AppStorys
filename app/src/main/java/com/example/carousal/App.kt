package com.example.carousal

import android.app.Application
import com.appversal.appstorys.AppStorys

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val attributes: List<Map<String, Any>> = listOf(
            mapOf("name" to "Alice", "age" to 25),
            mapOf("name" to "Bob", "age" to 30),
            mapOf("name" to "Charlie", "age" to 22)
        )

        // Initialize CampaignManager with userId and appId
        appStorys = AppStorys.getInstance(
            this,
            appId = "1163a1a2-61a8-486c-b263-7252f9a502c2", // Replace with App Id
            accountId = "5bb1378d-9f32-4da8-aed1-1ee44d086db7", // Replace with Account Id
            userId = "cheqtest",// Replace with User Id
            attributes = attributes,
            navigateToScreen =
            {
                navigateToScreen(it)
            })
    }


    //For Deeplinking with NavHost
    fun navigateToScreen(name: String) {

    }

    companion object {
        lateinit var appStorys: AppStorys
            private set
    }
}
