package com.appversal.appstorys

import android.app.Application
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appversal.appstorys.api.ApiRepository
import com.appversal.appstorys.api.BannerDetails
import com.appversal.appstorys.api.Campaign
import com.appversal.appstorys.api.RetrofitClient
import com.appversal.appstorys.api.TrackAction
import com.appversal.appstorys.api.WidgetDetails
import com.appversal.appstorys.api.WidgetImage
import com.appversal.appstorys.ui.AutoSlidingCarousel
import com.appversal.appstorys.ui.CarousalImage
import com.appversal.appstorys.ui.DoubleWidgets
import com.appversal.appstorys.ui.ImageCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppStorys private constructor(
    private val context: Application,
    private val appId: String,
    private val accountId: String,
    private val userId: String,
    private val attributes: List<Map<String, Any>>?,
    private val navigateToScreen: (String) -> Unit
) {

    private val _campaigns = MutableStateFlow<List<Campaign>>(emptyList())
    private val campaigns: StateFlow<List<Campaign>> get() = _campaigns

    private val _disabledCampaigns = MutableStateFlow<List<String>>(emptyList())
    private val disabledCampaigns: StateFlow<List<String>> get() = _disabledCampaigns

    private val _impressionsImages = MutableStateFlow<List<String>>(emptyList())
    private val impressionsImages: StateFlow<List<String>> get() = _impressionsImages


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

            val campaignsData = repository.getCampaignData(accessToken, userId, campaignList, attributes)
            Log.d("campaignsData", campaignsData.toString())

            _campaigns.emit(campaignsData.campaigns)
            Log.d("CampaignsValue", _campaigns.toString())
        }
    }

    fun getScreenCampaigns(screenName: String, positionList: List<String>) {
        coroutineScope.launch {
            if (accessToken.isNotEmpty()) {
                if (currentScreen != screenName) {
                    _disabledCampaigns.emit(emptyList())
                    _impressionsImages.emit(emptyList())
                    currentScreen = screenName
                }
                val campaignList = repository.getCampaigns(accessToken, currentScreen, positionList)
                Log.d("campaignList", campaignList.toString())

                val campaignsData = repository.getCampaignData(accessToken, userId, campaignList, attributes)
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

        val campaign = position?.let { pos -> campaignsData.value.filter { it.position == pos } }
            ?.firstOrNull { it.campaignType == "BAN" }
            ?: campaignsData.value.firstOrNull { it.campaignType == "BAN" }

        val bannerDetails = when (val details = campaign?.details) {
            is BannerDetails -> details
            else -> null
        }

        if (bannerDetails != null && !disabledCampaigns.value.contains(campaign?.id)) {
            val style = bannerDetails.styling
            val bannerUrl = bannerDetails.image
            val heightInDp: Dp? = bannerDetails.height?.dp

            LaunchedEffect(Unit) {
                campaign?.id?.let {
                    trackCampaignActions(it, "IMP")
                }

            }

            com.appversal.appstorys.ui.PinnedBanner(
                modifier = modifier.clickable {
                    campaign?.id?.let {
                        clickEvent(url = bannerDetails.link, campaignId = it)
                    }
                },
                imageUrl = bannerUrl,
                width = bannerDetails.width?.dp,
                exitIcon = style?.isClose ?: false,
                exitUnit = {
                    val ids: ArrayList<String> = ArrayList(_disabledCampaigns.value)
                    campaign?.id?.let {
                        ids.add(it)
                        coroutineScope.launch {
                            _disabledCampaigns.emit(ids.toList())
                        }
                    }

                },
                shape = RoundedCornerShape(
                    topStart = style?.topLeftBorderRadius?.dp ?: 16.dp,
                    topEnd = style?.topRightBorderRadius?.dp ?: 16.dp,
                    bottomEnd = style?.bottomRightBorderRadius?.dp ?: 0.dp,
                    bottomStart = style?.bottomLeftBorderRadius?.dp ?: 0.dp
                ),
                bottomMargin = style?.marginBottom?.dp ?: 0.dp,
                contentScale = contentScale,
                height = heightInDp ?: staticHeight,
                placeHolder = placeHolder
            )
        }
    }

    @Composable
    fun Widget(
        modifier: Modifier = Modifier,
        contentScale: ContentScale = ContentScale.Crop,
        staticHeight: Dp = 200.dp,
        placeHolder: Drawable?,
        position: String?
    ) {
        val campaignsData = campaigns.collectAsStateWithLifecycle()
        val campaign = position?.let { pos -> campaignsData.value.filter { it.position == pos } }
            ?.firstOrNull { it.campaignType == "WID" && it.details is WidgetDetails }
            ?: campaignsData.value.firstOrNull { it.campaignType == "WID" && it.details is WidgetDetails }
        if (campaign != null) {
            val widgetDetails = campaign?.details as WidgetDetails ?: null

            if (widgetDetails?.type == "full") (
                    FullWidget(
                        modifier = modifier,
                        staticHeight = staticHeight,
                        placeHolder = placeHolder,
                        position = position
                    )

                    ) else if (widgetDetails?.type == "half") {
                DoubleWidget(
                    modifier = modifier,
                    staticHeight = staticHeight,
                    position = position
                )
            }

        }

    }

    @Composable
    fun FullWidget(
        modifier: Modifier = Modifier,
        contentScale: ContentScale = ContentScale.Crop,
        staticHeight: Dp = 200.dp,
        placeHolder: Drawable?,
        position: String?
    ) {
        val campaignsData = campaigns.collectAsStateWithLifecycle()
        val disabledCampaigns = disabledCampaigns.collectAsStateWithLifecycle()

        val campaigns = position?.let { pos -> campaignsData.value.filter { it.position == pos } }
            ?.filter { it.campaignType == "WID" && it.details is WidgetDetails }
            ?: campaignsData.value.filter { it.campaignType == "WID" && it.details is WidgetDetails }

        val campaign = campaigns.firstOrNull { (it.details as WidgetDetails).type == "full" }
        val widgetDetails =
            if (campaign?.details != null) campaign.details as WidgetDetails else null

        if (widgetDetails != null && !disabledCampaigns.value.contains(campaign?.id) && widgetDetails.type == "full") {
            val pagerState = rememberPagerState(pageCount = {
                widgetDetails.widgetImages.count()
            })
            val heightInDp: Dp? = widgetDetails.height?.dp

            LaunchedEffect(pagerState.currentPage) {
                campaign?.id?.let {
                    trackCampaignActions(
                        it,
                        "IMP",
                        widgetDetails.widgetImages[pagerState.currentPage].id
                    )
                }
            }

            AutoSlidingCarousel(
                modifier = modifier,
                pagerState = pagerState,
                itemsCount = widgetDetails.widgetImages.count(),
                itemContent = { index ->
                    CarousalImage(
                        modifier = Modifier.clickable {
                            campaign?.id?.let {
                                clickEvent(
                                    url = widgetDetails.widgetImages[index].link,
                                    campaignId = it,
                                    widgetImageId = widgetDetails.widgetImages[index].id
                                )
                            }
                        },
                        contentScale = contentScale,
                        imageUrl = widgetDetails.widgetImages[index].image,
                        placeHolder = placeHolder,
                        height = heightInDp ?: staticHeight,
                    )
                }
            )

        }

    }

    @Composable
    fun DoubleWidget(
        modifier: Modifier = Modifier,
        staticHeight: Dp = 200.dp,
        position: String?
    ) {
        val campaignsData = campaigns.collectAsStateWithLifecycle()
        val disabledCampaigns = disabledCampaigns.collectAsStateWithLifecycle()

        val campaigns = position?.let { pos -> campaignsData.value.filter { it.position == pos } }
            ?.filter { it.campaignType == "WID" && it.details is WidgetDetails }
            ?: campaignsData.value.filter { it.campaignType == "WID" && it.details is WidgetDetails }

        val campaign = campaigns.firstOrNull { (it.details as WidgetDetails).type == "half" }
        val widgetDetails =
            if (campaign?.details != null) campaign.details as WidgetDetails else null


        if (widgetDetails != null && !disabledCampaigns.value.contains(campaign?.id) && widgetDetails.type == "half") {

            val heightInDp: Dp? = widgetDetails.height?.dp
            val widgetImagesPairs = widgetDetails.widgetImages.turnToPair()
            val pagerState = rememberPagerState(pageCount = {
                widgetImagesPairs.count()
            })

            LaunchedEffect(pagerState.currentPage) {
                campaign?.id?.let {
                    trackCampaignActions(
                        it,
                        "IMP",
                        widgetImagesPairs[pagerState.currentPage].first.id
                    )
                }

                campaign?.id?.let {

                    trackCampaignActions(
                        it,
                        "IMP",
                        widgetImagesPairs[pagerState.currentPage].second.id
                    )
                }

            }

            DoubleWidgets(
                modifier = modifier,
                pagerState = pagerState,
                itemsCount = widgetImagesPairs.count(),
                itemContent = { index ->
                    val (leftImage, rightImage) = widgetImagesPairs[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ImageCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    campaign?.id?.let {
                                        clickEvent(
                                            url = leftImage.link,
                                            campaignId = it,
                                            widgetImageId = leftImage.id
                                        )
                                    }
                                },
                            imageUrl = leftImage.image,
                            height = heightInDp ?: staticHeight
                        )

                        ImageCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    campaign?.id?.let {
                                        clickEvent(
                                            url = rightImage.link,
                                            campaignId = it,
                                            widgetImageId = rightImage.id
                                        )
                                    }
                                },
                            imageUrl = rightImage.image,
                            height = heightInDp ?: staticHeight
                        )
                    }
                }
            )
        }
    }

    private fun clickEvent(url: String, campaignId: String, widgetImageId: String? = null) {
        if (!isValidUrl(url)) {
            navigateToScreen(url)
        } else {
            openUrl(url)
        }

        Log.i("ClickImageId", widgetImageId.toString())
        trackCampaignActions(campaignId, "CLK", widgetImageId)

    }

    private fun List<WidgetImage>.turnToPair(): List<Pair<WidgetImage, WidgetImage>> {
        // Sort by order and pair consecutive elements
        val widgetImagePairs: List<Pair<WidgetImage, WidgetImage>> = this
            .sortedBy { it.order }
            .windowed(2, 2, partialWindows = false) { (first, second) ->
                first to second
            }

        return widgetImagePairs
    }

    private fun trackCampaignActions(
        campId: String,
        eventType: String,
        widgetImageId: String? = null
    ) {
        coroutineScope.launch {
            if (eventType != "CLK"){
                if (widgetImageId != null && !impressionsImages.value.contains(widgetImageId)) {
                    val impressions = ArrayList(impressionsImages.value)
                    impressions.add(widgetImageId)
                    _impressionsImages.emit(impressions)
                    repository.trackActions(
                        accessToken,
                        TrackAction(campId, userId, eventType, widgetImageId)
                    )
                } else if (!impressionsImages.value.contains(campId)){
                    val impressions = ArrayList(impressionsImages.value)
                    impressions.add(campId)
                    _impressionsImages.emit(impressions)
                    repository.trackActions(
                        accessToken,
                        TrackAction(campId, userId, eventType, null)
                    )
                }
            }else{
                repository.trackActions(accessToken, TrackAction(campId, userId, eventType, widgetImageId))
            }
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
        private var instance: AppStorys? = null

        fun getInstance(
            context: Application,
            appId: String,
            accountId: String,
            userId: String,
            attributes: List<Map<String, Any>>? = null,
            navigateToScreen: (String) -> Unit
        ): AppStorys {
            return instance ?: synchronized(this) {
                instance ?: AppStorys(
                    context = context,
                    appId = appId,
                    accountId = accountId,
                    userId = userId,
                    navigateToScreen = navigateToScreen,
                    attributes = attributes
                ).also { instance = it }
            }
        }
    }
}
