package com.example.carousal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.appversal.appstorys.ui.Pip
import com.example.carousal.ui.theme.CarousalTheme
import com.example.carousal.R
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CarousalTheme {
                MyApp()
            }
//            QuizScreen()
        }
    }
}

@Composable
fun MyApp() {
    val context = LocalContext.current

    var selectedItem by remember { mutableStateOf(0) } // Track selected nav item
//    var isSheetOpen by remember { mutableStateOf(false) }
    val campaignManager = App.appStorys


    Box {



        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {




                BottomNavigationBar(selectedItem) { index -> selectedItem = index }


            }


        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFFAF8F9)),
                contentAlignment = Alignment.Center // Centers content inside the Box
            ) {


//                Button(
//                    onClick = { isSheetOpen = true }
//                ) {
//                    Text(text = "Open Bottom Sheet")
//                }


//            if (isSheetOpen) {
//                BottomSheetComponent(onDismiss = { isSheetOpen = false })
//            }

//            Pip()


                when (selectedItem) {
                    0 -> HomeScreen()
                    1 -> PayScreen()
                }

            }
        }


//        campaignManager.CSAT(
//            modifier = Modifier.align(Alignment.BottomCenter),
//            position = null
//        )
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val campaignManager = App.appStorys

    campaignManager.getScreenCampaigns(
        "Home Screen",
        listOf("widget_one", "widget_two", "widget_three", "widget_four", "widget_fifty")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8F8)) // Optional background color
    ) {
        // Scrollable Column using LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally // Center align items horizontally
        ) {
            item {

                Image(
                    painter = painterResource(id = R.drawable.home_top), // Use the image from drawable
                    contentDescription = "Home Screen Top Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.FillWidth // Ensures the image fits the screen width
                )

                campaignManager.Widget(
                    modifier = Modifier.fillMaxWidth(),
                    placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                    position = "widget_one"
                )

//                Spacer(modifier = Modifier.height(12.dp))

                campaignManager.Widget(
                    modifier = Modifier.fillMaxWidth(),
                    placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                    position = "widget_two"
                )

                Spacer(modifier = Modifier.height(12.dp))

                campaignManager.Widget(
                    modifier = Modifier.fillMaxWidth(),
                    placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                    position = "widget_three"
                )

                Spacer(modifier = Modifier.height(28.dp))

                campaignManager.Widget(
                    modifier = Modifier.fillMaxWidth(),
                    placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                    position = "widget_fifty"
                )

                Spacer(modifier = Modifier.height(12.dp))

                campaignManager.Widget(
                    modifier = Modifier.fillMaxWidth(),
                    placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
                    position = "widget_four"
                )

                Spacer(modifier = Modifier.height(20.dp))


//                campaignManager.PinnedBanner(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 16.dp),
//                    placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
//                    position = "banner_top"
//                )
//
//                campaignManager.PinnedBanner(
//                    modifier = Modifier.fillMaxWidth(),
//                    placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
//                    position = "banner_bottom"
//                )
            }
        }


        campaignManager.Floater(
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}


@Composable
fun PayScreen() {

    val context = LocalContext.current
    val campaignManager = App.appStorys

    var isSheetOpen by remember { mutableStateOf(false) }

    campaignManager.getScreenCampaigns(
        "Pay Screen",
        listOf("banner_bottom")
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.pay_screen_top), // Use the image from drawable
                contentDescription = "Pay Screen Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth // Ensures the image fits the screen width
            )

            Image(
                painter = painterResource(id = R.drawable.pay_screen_bottom), // Use the image from drawable
                contentDescription = "Pay Screen Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable { isSheetOpen = true },
                contentScale = ContentScale.FillWidth // Ensures the image fits the screen width
            )
        }

        if (isSheetOpen) {
            BottomSheetComponent(onDismiss = { isSheetOpen = false })
        }

        campaignManager.PinnedBanner(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            placeHolder = context.getDrawable(R.drawable.ic_launcher_foreground),
            position = "banner_bottom"
        )

    }
}

@Composable
fun BottomNavigationBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White
    ) {
        val items = listOf("Home" to Icons.Default.Home, "Pay" to Icons.Default.Person)

        items.forEachIndexed { index, pair ->
            NavigationBarItem(
                icon = {
                    Icon(
                        pair.second,
                        contentDescription = pair.first,
                        tint = if (selectedIndex == index) Color(0xFF01C198) else Color.Gray
                    )
                },
                label = {
                    Text(
                        pair.first,
                        color = if (selectedIndex == index) Color(0xFF01C198) else Color.Gray
                    )
                },
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetComponent(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image

            Image(
                painter = painterResource(id = R.drawable.pop_up), // Use the image from drawable
                contentDescription = "Pay Screen Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth // Ensures the image fits the screen width
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), // Space from left and right
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // First Button: Solid Green
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp), // Standard height
                    shape = RoundedCornerShape(12.dp), // Rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF01C198), // Green background
                        contentColor = Color.White // White text
                    )
                ) {
                    Text(
                        text = "CONTINUE WITH APPLICATION",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp)) // Space between buttons

                // Second Button: Outlined (White Background, Green Border)
                OutlinedButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp), // Standard height
                    shape = RoundedCornerShape(12.dp), // Rounded corners
                    border = BorderStroke(1.dp, Color(0xFF01C198)), // Green border
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White, // White background
                        contentColor = Color(0xFF01C198) // Green text
                    )
                ) {
                    Text(text = "GO TO HOME", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    CarousalTheme {
        MyApp()
    }
}


// package com.example.carousal

// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.enableEdgeToEdge
// import androidx.compose.foundation.background
// import androidx.compose.foundation.layout.*
// import androidx.compose.material3.Button
// import androidx.compose.material3.Scaffold
// import androidx.compose.material3.Text
// import androidx.compose.runtime.*
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.tooling.preview.Preview
// import com.example.carousal.ui.theme.CarousalTheme
// import com.appversal.appstorys.AppStorys


// class MainActivity : ComponentActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)

//         enableEdgeToEdge()
//         setContent {
//             CarousalTheme {
//                 MyApp()
//             }
//         }
//     }
// }

// @Composable
// fun MyApp() {
//     val context = LocalContext.current
//     val campaignManager = App.appStorys

//     campaignManager.getScreenCampaigns(
//         "Home Screen",
//         listOf("banner_top", "banner_bottom", "widget_top", "widget_center", "widget_bottom")
//     )


//     Scaffold(
//         modifier = Modifier.fillMaxSize()) {
//         innerPadding ->
//         Box(
//             modifier = Modifier
//                 .fillMaxSize()
//                 .padding(innerPadding)
//                 .background(Color(0xFFFAF8F9)),
//             contentAlignment = Alignment.Center
//         ) {


//             campaignManager.CSAT(
//                 modifier = Modifier.align(Alignment.BottomCenter),
//                 position =  null
//             )

//             campaignManager.Floater(modifier = Modifier.align(Alignment.BottomEnd))
//         }
//     }
// }

// @Preview(showBackground = true)
// @Composable
// fun MyAppPreview() {
//     CarousalTheme {
//         MyApp()
//     }
// }
