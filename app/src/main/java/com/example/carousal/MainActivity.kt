package com.example.carousal

import android.R.attr.height
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.appversal.appstorys.ApiRepository
import com.appversal.appstorys.AutoSlidingCarousel
import com.appversal.appstorys.CampaignDetail
import com.appversal.appstorys.CarousalImage
import com.appversal.appstorys.HomeViewModel
import com.appversal.appstorys.PinnedBanner
import com.appversal.appstorys.RetrofitClient
import com.appversal.appstorys.ViewModelFactory
import com.example.carousal.ui.theme.CarousalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.apiService
        val repository = ApiRepository(apiService)
        val factory = ViewModelFactory(repository)
        val viewModel: HomeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val images = listOf(
            "https://images.pexels.com/photos/2486168/pexels-photo-2486168.jpeg",
        )
        enableEdgeToEdge()
        setContent {

            val banners by viewModel.banners.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.fetchData()  // Fetch data when UI loads
            }

            LaunchedEffect(Unit) {
                for (banner in banners) {
                    Log.d("banner", banner.toString())
                }
            }

            // i want to get the url of image from details where campaign type is BAN
            // pass in url parameter below
            val pagerState = rememberPagerState(pageCount = {
                images.count()
            })
            val bannerUrl by remember { mutableStateOf(banners.firstOrNull { it.campaign_type == "BAN" }?.details as? CampaignDetail) }
            Log.d("bannerUrl", bannerUrl.toString())
            CarousalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        if(banners.isNotEmpty()){
                            PinnedBanner(
                                modifier = Modifier.align(Alignment.Center),
                                url =   bannerUrl?.thumbnail?:"" ,
                                height = 100.dp,
                                placeHolder = getDrawable(R.drawable.ic_launcher_background)
                            )
                        }
                    }
                }
            }
        }
    }
}
