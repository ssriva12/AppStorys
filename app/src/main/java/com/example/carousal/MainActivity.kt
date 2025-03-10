package com.example.carousal

import android.os.Bundle
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

    campaignManager.getScreenCampaigns(
        "Home Screen",
        listOf("widget_one", "widget_three", "widget_fifty", "widget_four")
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
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                AllCampaigns()
            }
        }


    }


}

@Composable
fun AllCampaigns() {
    val campaignManager = App.appStorys
    val context = LocalContext.current
//    var showPip by remember { mutableStateOf(true) }
    Box {
        LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White), horizontalAlignment = Alignment.CenterHorizontally) {

            item {

                campaignManager.Widget(
                    placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                    position = "widget_one"
                )

            }



            item {
                campaignManager.Widget(
                    placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                    position = "widget_fifty"
                )
            }

            item {
                campaignManager.Reels()
            }


            item {
                campaignManager.Stories()
            }

            item {
                campaignManager.ToolTipWrapper(
                    targetModifier = Modifier,
                    targetKey = "about_button",
                ) {
                    Image(
                        modifier = it.size(60.dp),
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = "Home Screen Top Image",
//                        modifier = Modifier
//                            .size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            item {
                Spacer(Modifier.height(100.dp))
            }

            item{
                campaignManager.ToolTipWrapper(
                    targetModifier = Modifier,
                    targetKey = "home_button",
                ) {
                    Button(
                        modifier = it,
                        onClick = {}
                    ) {
                    }
                }
            }

            item {
                campaignManager.Widget(
                    placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                    position = "widget_four"
                )
            }


        }

//        campaignManager.CSAT(modifier = Modifier.align(Alignment.BottomCenter))

        campaignManager.Floater(boxModifier = Modifier.align(Alignment.BottomCenter))

        campaignManager.Pip()

        campaignManager.ShowCaseScreen()

    }

}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    CarousalTheme {
        MyApp()
    }
}
