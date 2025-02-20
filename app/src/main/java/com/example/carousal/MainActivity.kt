package com.example.carousal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appversal.appstorys.PinnedBanner
import com.example.carousal.ui.theme.CarousalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val images = listOf(
            "https://images.pexels.com/photos/2486168/pexels-photo-2486168.jpeg",
        )
        enableEdgeToEdge()
        setContent {
            val pagerState = rememberPagerState(pageCount = {
                images.count()
            })
            CarousalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {

                        PinnedBanner(
                            modifier = Modifier.align(Alignment.Center),
                            url = "https://images.pexels.com/photos/2486168/pexels-photo-2486168.jpeg",
                            height = 100.dp,
                            placeHolder = getDrawable(R.drawable.ic_launcher_background)
                        )


                    }
                }
            }
        }
    }
}
