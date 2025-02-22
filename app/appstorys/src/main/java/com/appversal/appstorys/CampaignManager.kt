package com.appversal.appstorys

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CampaignManager private constructor(
    private val context: Application,
    private val appId: String,
    private val accountId: String,
    private val navigateToScreen: (String) -> Unit
) {

    private val _campaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val campaigns: StateFlow<List<Campaign>> get() = _campaigns

    private val _disabledCampaigns = MutableStateFlow<List<String>>(emptyList())
    val disabledCampaigns: StateFlow<List<String>> get() = _disabledCampaigns


    private val apiService = RetrofitClient.apiService
    private val repository = ApiRepository(apiService)
    private var accessToken = ""
    private var currentScreen = ""

    private var isDataFetched = false
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        coroutineScope.launch {
            fetchData()
        }
    }


    private suspend fun fetchData() {
        if (isDataFetched) return
        isDataFetched = true

        val accessToken = repository.getAccessToken(appId, accountId)
        Log.d("accessToken", accessToken.toString())

        if (accessToken != null) {
            this.accessToken = accessToken
            currentScreen = "Home Screen"
            val campaignList = repository.getCampaigns(accessToken, currentScreen, null)
            Log.d("campaignList", campaignList.toString())

            val campaignsData = repository.getCampaignData(accessToken, campaignList)
            Log.d("campaignsData", campaignsData.toString())

            _campaigns.emit(campaignsData.campaigns)
            Log.d("CampaignsValue", _campaigns.toString())
        }
    }

    fun getScreenCampaigns(screenName: String, positionList: List<String>){
        coroutineScope.launch {
            if (accessToken.isNotEmpty()) {
                if (currentScreen != screenName){
                    _disabledCampaigns.emit(emptyList())
                    currentScreen = screenName
                }
                val campaignList = repository.getCampaigns(accessToken, currentScreen, positionList)
                Log.d("campaignList", campaignList.toString())

                val campaignsData = repository.getCampaignData(accessToken, campaignList)
                Log.d("campaignsData", campaignsData.toString())

                _campaigns.emit(campaignsData.campaigns)
                Log.d("CampaignsValue", _campaigns.toString())
            }
        }
    }

    @Composable
    fun PinnedBanner(
        modifier: Modifier = Modifier,
        contentScale: ContentScale = ContentScale.Crop,
        staticHeight: Dp = 200.dp,
        placeHolder: Drawable?,
        position: String?
    ) {
        val campaignsData = campaigns.collectAsStateWithLifecycle()
        val disabledCampaigns = disabledCampaigns.collectAsStateWithLifecycle()

        var sendImpression by remember { mutableStateOf(false) }
        val campaign = campaignsData.value.filter { it.position == position }.firstOrNull { it.campaignType == "BAN" }

        val bannerDetails = when (val details = campaign?.details) {
            is BannerDetails -> details
            is WidgetDetails -> null
            null -> null
        }

        if (bannerDetails != null && !disabledCampaigns.value.contains(campaign?.id)){
            val style = bannerDetails.styling
            val bannerUrl = bannerDetails.image
            val heightInDp: Dp? = bannerDetails.height?.dp

            LaunchedEffect(Unit) {
                if(!sendImpression){
                    sendImpression = true
                    campaign?.id?.let{
                        trackCampaignActions(it, "IMP")
                    }
                }
            }

            PinnedBanner(
                modifier = modifier.clickable {
                    if (!isValidUrl(bannerDetails.link)){
                        navigateToScreen(bannerDetails.link)
                    }else{
                        openUrl(bannerDetails.link)
                    }

                    campaign?.id?.let{
                        trackCampaignActions(it, "CLK")
                    }
                },
                url = bannerUrl,
                width = bannerDetails.width?.dp,
                exitIcon = style?.isClose ?: false,
                exitUnit = {
                    val ids : ArrayList<String> = ArrayList(_disabledCampaigns.value)
                    campaign?.id?.let {
                        ids.add(it)
                        coroutineScope.launch{
                            _disabledCampaigns.emit(ids.toList())
                        }
                    }

                },
                shape = RoundedCornerShape(topStart = style?.topLeftBorderRadius?.dp ?: 16.dp, topEnd = style?.topRightBorderRadius?.dp ?: 16.dp, bottomEnd = style?.bottomRightBorderRadius?.dp ?: 0.dp, bottomStart = style?.bottomLeftBorderRadius?.dp ?: 0.dp),
                bottomMargin = style?.marginBottom?.dp ?: 0.dp,
                contentScale = contentScale,
                height = heightInDp ?: staticHeight,
                placeHolder = placeHolder
            )
        }
    }

    private fun trackCampaignActions(campId: String, eventType: String){
        coroutineScope.launch {
            repository.trackActions(accessToken, TrackAction(campId, accountId, eventType))
        }
    }


    private fun isValidUrl(url: String?): Boolean {
        return !url.isNullOrEmpty() && Patterns.WEB_URL.matcher(url).matches()
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    companion object {
        @Volatile
        private var instance: CampaignManager? = null

        fun getInstance(context: Application, appId: String, accountId: String, navigateToScreen: (String) -> Unit): CampaignManager {
            return instance ?: synchronized(this) {
                instance ?: CampaignManager(context, appId, accountId, navigateToScreen).also { instance = it }
            }
        }
    }
}
