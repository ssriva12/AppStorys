package com.example.carousal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.appversal.appstorys.PinnedBanner
import com.appversal.appstorys.campaign.CampaignService
import com.example.carousal.ui.theme.CarousalTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var campaignService: CampaignService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize campaigns
        lifecycleScope.launch {
            campaignService.initializeCampaigns(
                accountId = "your_account_id",
                appId = "your_app_id",
                userId = "user_id",
                screenName = "main_screen"
            )
        }

        val images = listOf(
            "https://images.pexels.com/photos/2486168/pexels-photo-2486168.jpeg",
        )
        enableEdgeToEdge()
        setContent {
            CarousalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        PinnedBanner(
                            modifier = Modifier.align(Alignment.Center),
                            campaignService = campaignService,
                            height = 100.dp,
                            placeHolder = getDrawable(R.drawable.ic_launcher_background)
                        )
                    }
                }
            }
        }
    }
}
