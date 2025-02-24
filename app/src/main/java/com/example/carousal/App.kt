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
            appId = "afadf960-3975-4ba2-933b-fac71ccc2002", // Replace with App Id
            accountId = "13555479-077f-445e-87f0-e6eae2e215c5", // Replace with Account Id
            userId = "sbcubsdvid",// Replace with User Id
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
