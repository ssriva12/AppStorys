package com.example.carousal

import android.app.Application
import com.appversal.appstorys.CampaignManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize CampaignManager with userId and appId
        campaignManager = CampaignManager.getInstance(
            this,
            appId = "afadf960-3975-4ba2-933b-fac71ccc2002",
            accountId = "13555479-077f-445e-87f0-e6eae2e215c5",
            {
                navigateToScreen(it)
            })
    }


    fun navigateToScreen(name: String) {

    }

    companion object {
        lateinit var campaignManager: CampaignManager
            private set
    }
}
