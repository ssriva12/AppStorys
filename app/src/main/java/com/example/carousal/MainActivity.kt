package com.example.carousal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appversal.appstorys.ui.BottomSheetComponent
import com.appversal.appstorys.ui.CsatDialog
import com.appversal.appstorys.ui.OverlayFloater
import com.appversal.appstorys.ui.Pip
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
        listOf("banner_top", "banner_bottom", "widget_top", "widget_center", "widget_bottom")
    )

//    var selectedItem by remember { mutableStateOf(0) } // Track selected nav item
    var isSheetOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
//        bottomBar = {
//            BottomNavigationBar(selectedItem) { index -> selectedItem = index }
//        }


    ) {
        innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFAF8F9)),
            contentAlignment = Alignment.Center // Centers content inside the Box
        ) {

            var showCsat by remember { mutableStateOf(true) }

            Column (

            ){
                if (showCsat) {
                    CsatDialog(
                        onDismiss = { showCsat = false },
                        onSubmitFeedback = { feedback ->
                            println("Received feedback: $feedback")
                        }
                    )
                }

                Button(
                    onClick = { isSheetOpen = true }
                ) {
                    Text(text = "Open Bottom Sheet")
                }
            }


            if (isSheetOpen) {
                BottomSheetComponent(onDismiss = { isSheetOpen = false })
            }

            OverlayFloater()

//            Pip()
//            campaignManager.Widget(
//                modifier = Modifier
//                    .align(Alignment.TopCenter)
//                    .padding(top = 16.dp), // Add padding to avoid overlap with status bar
//                placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
//                position = "widget_top"
//            )

//            campaignManager.Widget(
//                modifier = Modifier.align(Alignment.Center),
//                placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
//                position = "widget_center"
//            )

//            campaignManager.Widget(
//                modifier = Modifier.align(Alignment.BottomCenter),
//                placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
//                position = "widget_bottom"
//            )

//            campaignManager.PinnedBanner(
//                modifier = Modifier
//                    .align(Alignment.TopCenter)
//                    .padding(top = 16.dp), // Safe area handling
//                placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
//                position = "banner_top"
//            )

//            campaignManager.PinnedBanner(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter),
//                placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
//                position = "banner_bottom"
//            )
        }
    }
}

//@Composable
//fun BottomNavigationBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
//    NavigationBar(
//        containerColor = Color.White
//    ) {
//        val items = listOf("Home" to Icons.Default.Home, "Search" to Icons.Default.Search)
//
//        items.forEachIndexed { index, pair ->
//            NavigationBarItem(
//                icon = { Icon(pair.second, contentDescription = pair.first, tint = if (selectedIndex == index) Color(0xFF01C198) else Color.Gray) },
//                label = { Text(pair.first, color = if (selectedIndex == index) Color(0xFF01C198) else Color.Gray) },
//                selected = selectedIndex == index,
//                onClick = { onItemSelected(index) }
//            )
//        }
//    }
//}


@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    CarousalTheme {
        MyApp()
    }
}
