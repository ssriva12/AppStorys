package com.example.carousal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.carousal.ui.theme.CarousalTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val app = LocalContext.current.applicationContext as App
    val screenName by app.screenNameNavigation.collectAsState()
    var currentScreen by remember { mutableStateOf("HomeScreen") }

    LaunchedEffect(screenName) {
        if (screenName.isNotEmpty() && currentScreen != screenName){
            currentScreen = screenName
            app.resetNavigation()
        }
    }
    campaignManager.getScreenCampaigns(
        "Home Screen",
        listOf("widget_one", "widget_three", "widget_fifty", "widget_four"),
        listOf("button_one", "button_two")
    )

    var edgeToEdgePadding by remember { mutableStateOf(PaddingValues()) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {

//                    campaignManager.ToolTipWrapper(
//                        targetModifier = Modifier,
//                        targetKey = "about_button",
//                    ) {
//                        Button(modifier = it, onClick = {}) { }
//                    }

            }
        ) { innerPadding ->
            edgeToEdgePadding = innerPadding
            if (currentScreen == "PayScreen"){
                PayScreen()
            }else{
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    AllCampaigns()
                }
            }
        }

        campaignManager.ShowCaseScreen()

    }


}

@Composable
fun AllCampaigns() {
    val campaignManager = App.appStorys
    val context = LocalContext.current

    val bannerHeight = campaignManager.getBannerHeight()

    Log.i("BannerHeight", bannerHeight.toString())
//    var showPip by remember { mutableStateOf(true) }
    Box {
        Column(modifier = Modifier.align(Alignment.Center)){
            campaignManager.PinnedBanner(modifier = Modifier, contentScale = ContentScale.FillWidth, placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground), position = null)

            campaignManager.Widget(modifier = Modifier, contentScale = ContentScale.FillWidth, placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground), position = null)

        }
    }

}



@Composable
fun PayScreen() {

    Box {

    }

}

//@Preview(showBackground = true)
//@Composable
//fun MyAppPreview() {
//    CarousalTheme {
//        MyApp()
//    }
//}
