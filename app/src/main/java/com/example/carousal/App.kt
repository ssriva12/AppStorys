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

        //1163a1a2-61a8-486c-b263-7252f9a502c2
        //5bb1378d-9f32-4da8-aed1-1ee44d086db7

        //afadf960-3975-4ba2-933b-fac71ccc2002
        //13555479-077f-445e-87f0-e6eae2e215c5

        // Initialize CampaignManager with userId and appId
        AppStorys.initialize(
            context = this,
            appId = "1163a1a2-61a8-486c-b263-7252f9a502c2",
            accountId = "5bb1378d-9f32-4da8-aed1-1ee44d086db7",
            userId = "cheqtest",
            attributes = attributes,
            navigateToScreen = { screen ->
                println("Navigating to $screen")
                navigateToScreen(screen)
            }
        )

        appStorys = AppStorys
    }


    //For Deeplinking with NavHost
    fun navigateToScreen(name: String) {

    }

    companion object {
        lateinit var appStorys: AppStorys
            private set
    }
}
