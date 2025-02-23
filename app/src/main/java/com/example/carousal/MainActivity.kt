package com.example.carousal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    val campaignManager = App.appStorys
    campaignManager.getScreenCampaigns("Home Screen", listOf("banner_top", "banner_bottom", "widget_top","widget_center", "widget_bottom"))

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            campaignManager.Widget(
                modifier = Modifier.align(Alignment.TopCenter),
                placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                position = "widget_top"
            )

            campaignManager.Widget(
                modifier = Modifier.align(Alignment.Center),
                placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                position = "widget_center"
            )

//            campaignManager.Widget(
//                modifier = Modifier.align(Alignment.BottomCenter),
//                placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
//                position = "widget_bottom"
//            )


            /*campaignManager.PinnedBanner(
                modifier = Modifier.align(Alignment.TopCenter),
                placeHolder = context.getDrawable(com.example.carousal.R.drawable.ic_launcher_foreground),
                position = "banner_top"
            )*/


            campaignManager.PinnedBanner(
                modifier = Modifier.align(Alignment.BottomCenter),
                placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                position = "banner_bottom"
            )
        }
    }
}