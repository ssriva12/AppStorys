package com.example.carousal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.appversal.appstorys.ApiRepository
import com.appversal.appstorys.BannerDetails
import com.appversal.appstorys.CampaignManager
import com.appversal.appstorys.PinnedBanner
import com.appversal.appstorys.R
import com.appversal.appstorys.RetrofitClient
import com.appversal.appstorys.WidgetDetails
import com.example.carousal.ui.theme.CarousalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            CarousalTheme {
                MyApp()
            }
        }
    }
}


@Composable
fun MyApp() {
    val context = LocalContext.current

    val campaignManager = App.campaignManager
    campaignManager.getScreenCampaigns("Home Screen", listOf("banner_top", "banner_bottom"))

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {


            campaignManager.PinnedBanner(
                modifier = Modifier.align(Alignment.TopCenter),
                placeHolder = context.getDrawable(com.example.carousal.R.drawable.ic_launcher_foreground),
                position = "banner_top"
            )


            campaignManager.PinnedBanner(
                modifier = Modifier.align(Alignment.BottomCenter).clickable {

                },
                placeHolder = context.getDrawable(com.example.carousal.R.drawable.ic_launcher_foreground),
                position = "banner_bottom"
            )
        }
    }
}