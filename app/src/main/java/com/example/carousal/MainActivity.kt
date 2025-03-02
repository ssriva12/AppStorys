package com.example.carousal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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

    campaignManager.getScreenCampaigns(
        "Home Screen",
        listOf("widget_one", "widget_three", "widget_fifty", "widget_four")
    )


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()) {
                innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFFAF8F9)),
            ) {

                AllCampaigns()

            }
        }

    }

}

@Composable
fun AllCampaigns(){
    val campaignManager = App.appStorys
    val context = LocalContext.current
    Box{
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item{
                campaignManager.PinnedBanner(placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground), position = null)
            }

            item{
                campaignManager.Widget(placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground), position = "widget_one")
            }


            item{
                campaignManager.Widget(placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground), position = "widget_three")
            }

            item{
                campaignManager.Widget(placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground), position = "widget_fifty")
            }

            item{
                campaignManager.Reels()
            }


            item{
                campaignManager.Stories()
            }

            item{
                campaignManager.Widget(placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground), position = "widget_four")
            }


        }
        campaignManager.CSAT(modifier = Modifier.align(Alignment.BottomCenter))

        campaignManager.Floater(boxModifier = Modifier.align(Alignment.BottomCenter))

    }

}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    CarousalTheme {
        MyApp()
    }
}
