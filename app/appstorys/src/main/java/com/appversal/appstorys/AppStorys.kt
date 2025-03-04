package com.appversal.appstorys

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appversal.appstorys.api.ApiRepository
import com.appversal.appstorys.api.BannerDetails
import com.appversal.appstorys.api.CSATDetails
import com.appversal.appstorys.api.Campaign
import com.appversal.appstorys.api.CsatFeedbackPostRequest
import com.appversal.appstorys.api.FloaterDetails
import com.appversal.appstorys.api.ReelsDetails
import com.appversal.appstorys.api.RetrofitClient
import com.appversal.appstorys.api.TrackAction
import com.appversal.appstorys.api.WidgetDetails
import com.appversal.appstorys.api.WidgetImage
import com.appversal.appstorys.ui.AutoSlidingCarousel
import com.appversal.appstorys.ui.CarousalImage
import com.appversal.appstorys.ui.CsatDialog
import com.appversal.appstorys.ui.DoubleWidgets
import com.appversal.appstorys.ui.FullScreenVideoScreen
import com.appversal.appstorys.ui.ImageCard
import com.appversal.appstorys.ui.OverlayFloater
import com.appversal.appstorys.ui.ReelsRow
import com.appversal.appstorys.ui.ShowcaseHighlight
import com.appversal.appstorys.ui.ShowcaseView
import com.appversal.appstorys.ui.TooltipContent
import com.appversal.appstorys.ui.TooltipPopup
import com.appversal.appstorys.ui.TooltipPopupPosition
import com.appversal.appstorys.ui.calculateTooltipPopupPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.appversal.appstorys.api.ReelActionRequest
import com.appversal.appstorys.api.ReelStatusRequest
import com.appversal.appstorys.api.StoryGroup
import com.appversal.appstorys.api.Tooltip
import com.appversal.appstorys.api.TooltipsDetails
import com.appversal.appstorys.api.TrackActionStories
import com.appversal.appstorys.api.TrackActionTooltips
import com.appversal.appstorys.ui.StoryAppMain
import com.appversal.appstorys.ui.getLikedReels
import com.appversal.appstorys.ui.saveLikedReels
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

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

    private val _impressions = MutableStateFlow<List<String>>(emptyList())
    private val impressions: StateFlow<List<String>> get() = _impressions

    private val _viewsCoordinates = MutableStateFlow<Map<String, LayoutCoordinates>>(emptyMap())
    private val viewsCoordinates: StateFlow<Map<String, LayoutCoordinates>> =
        _viewsCoordinates.asStateFlow()

    private val _tooltipTargetView = MutableStateFlow<Tooltip?>(null)
    private val tooltipTargetView: StateFlow<Tooltip?> = _tooltipTargetView.asStateFlow()

    private val _tooltipViewed = MutableStateFlow<List<String>>(emptyList())
    private val tooltipViewed: StateFlow<List<String>> = _tooltipViewed.asStateFlow()

    private val _showcaseVisible = MutableStateFlow(false)
    private val showcaseVisible: StateFlow<Boolean> = _showcaseVisible.asStateFlow()

    private val _selectedReelIndex = MutableStateFlow<Int>(0)
    private val selectedReelIndex: StateFlow<Int> = _selectedReelIndex.asStateFlow()

    private val _reelFullScreenVisible = MutableStateFlow(false)
    private val reelFullScreenVisible: StateFlow<Boolean> = _reelFullScreenVisible.asStateFlow()

    private val apiService = RetrofitClient.apiService
    private val repository = ApiRepository(apiService)
    private var accessToken = ""
    private var currentScreen = ""

    private var showCsat = false

    private var isDataFetched = false
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        coroutineScope.launch {
            fetchData()
            showCaseInformation()
        }
    }


    private suspend fun fetchData() {
        if (isDataFetched) return
        isDataFetched = true

        try {
            val accessToken = repository.getAccessToken(appId, accountId)
            Log.d("accessToken", accessToken.toString())

            if (accessToken != null) {
                this.accessToken = accessToken
                currentScreen = "Home Screen"
                val campaignList = repository.getCampaigns(accessToken, currentScreen, null)
                Log.d("campaignList", campaignList.toString())

                if (campaignList?.isNotEmpty() == true) {
                    val campaignsData =
                        repository.getCampaignData(accessToken, userId, campaignList, attributes)
                    Log.d("campaignsData", campaignsData.toString())

                    campaignsData?.campaigns?.let { _campaigns.emit(it) }
                    Log.d("CampaignsValue", _campaigns.toString())
                }

            }
        } catch (exception: Exception) {
            Log.e("AppStorys", exception.message ?: "Error Fetch Data")
        }

    }

    fun getScreenCampaigns(screenName: String, positionList: List<String>) {
        try {
            coroutineScope.launch {
                if (accessToken.isNotEmpty()) {
                    if (currentScreen != screenName) {
                        _disabledCampaigns.emit(emptyList())
                        _impressions.emit(emptyList())
                        currentScreen = screenName
                    }
                    val campaignList =
                        repository.getCampaigns(accessToken, currentScreen, positionList)
                    Log.d("campaignList", campaignList.toString())

                    if (campaignList?.isNotEmpty() == true) {
                        val campaignsData =
                            repository.getCampaignData(
                                accessToken,
                                userId,
                                campaignList,
                                attributes
                            )
                        Log.d("campaignsData", campaignsData.toString())

                        campaignsData?.campaigns?.let { _campaigns.emit(it) }
                        Log.d("CampaignsValue", _campaigns.toString())
                    }
                }
            }
        } catch (exception: Exception) {
            Log.e("AppStorys", exception.message ?: "Error Fetch Data")
        }
    }

    fun trackEvents(
        event_type: String,
        campaign_id: String? = null,
        metadata: Map<String, Any>? = null
    ) {
        coroutineScope.launch {
            if (accessToken.isNotEmpty()) {
                try {
                    val requestBody = JSONObject().apply {
                        put("user_id", userId)
                        put("event_type", event_type)
                        campaign_id?.let { put("campaign_id", it) }
                        metadata?.let { put("metadata", it) }
                    }

                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://tracking.appstorys.com/capture-event") // Replace with your actual API endpoint
                        .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()

                    val response = client.newCall(request).execute()

//                    Log.i("eventTrack", response.code.toString())
                } catch (e: Exception) {
                    // Handle exception
                    e.printStackTrace()
//                    Log.i("eventTrack", e.toString())
                }
            }
        }
    }

    @Composable
    fun CSAT(
        modifier: Modifier = Modifier,
        displayDelaySeconds: Long = 10,
        position: String? = null
    ) {

        if (!showCsat) {
            val campaignsData = campaigns.collectAsStateWithLifecycle()

            val campaign =
                position?.let { pos -> campaignsData.value.filter { it.position == pos } }
                    ?.firstOrNull { it.campaignType == "CSAT" }
                    ?: campaignsData.value.firstOrNull { it.campaignType == "CSAT" }

            val csatDetails = when (val details = campaign?.details) {
                is CSATDetails -> details
                else -> null
            }

            if (csatDetails != null) {
                val style = csatDetails.styling
                var isVisibleState by remember { mutableStateOf(false) }
                val updatedDelay by rememberUpdatedState(
                    style?.displayDelay?.toLong() ?: displayDelaySeconds
                )

                LaunchedEffect(Unit) {
                    campaign?.id?.let {
                        trackCampaignActions(it, "IMP")
                    }
                    delay(updatedDelay * 1000)
                    isVisibleState = true
                }

                AnimatedVisibility(
                    modifier = modifier,
                    visible = isVisibleState,
                    enter = slideInVertically() { it },
                    exit = slideOutVertically { it }
                ) {
                    CsatDialog(
                        onDismiss = {
                            isVisibleState = false
                            coroutineScope.launch {
                                delay(500L)
                                showCsat = true
                            }
                        },
                        onSubmitFeedback = { feedback ->
                            coroutineScope.launch {
                                repository.captureCSATResponse(
                                    accessToken,
                                    CsatFeedbackPostRequest(
                                        user_id = userId,
                                        csat = csatDetails.id,
                                        rating = feedback.rating,
                                        additional_comments = feedback.additionalComments,
                                        feedback_option = feedback.feedbackOption
                                    )

                                )
                            }
                            println("Received feedback: $feedback")
                        },
                        csatDetails = csatDetails
                    )
                }
            }
        }
    }

    @Composable
    fun Floater(
        boxModifier: Modifier = Modifier,
        iconModifier: Modifier = Modifier
    ) {
        val campaignsData = campaigns.collectAsStateWithLifecycle()

        val campaign =
            campaignsData.value.firstOrNull { it.campaignType == "FLT" && it.details is FloaterDetails }

        val floaterDetails = when (val details = campaign?.details) {
            is FloaterDetails -> details
            else -> null
        }

        if (floaterDetails != null && !floaterDetails.image.isNullOrEmpty()) {
            LaunchedEffect(Unit) {
                campaign?.id?.let {
                    trackCampaignActions(it, "IMP")
                }

            }


            Box(modifier = boxModifier.fillMaxWidth()) {
                val alignmentModifier = when (floaterDetails.position) {
                    "right" -> Modifier.align(Alignment.BottomEnd)
                    "left" -> Modifier.align(Alignment.BottomStart)
                    else -> Modifier.align(Alignment.BottomStart)
                }

                OverlayFloater(
                    modifier = iconModifier.then(alignmentModifier),
                    onClick = {
                        if (campaign?.id != null && floaterDetails.link != null) {
                            clickEvent(url = floaterDetails.link, campaignId = campaign.id)
                        }

                    },
                    image = floaterDetails.image,
                    height = floaterDetails.height?.dp ?: 60.dp,
                    width = floaterDetails.width?.dp ?: 60.dp
                )
            }

        }
    }


    @Composable
    fun ToolTipWrapper(
        targetModifier: Modifier,
        targetKey: String,
        requesterView: @Composable (Modifier) -> Unit
    ) {
        var position by remember { mutableStateOf(TooltipPopupPosition()) }
        val view = LocalView.current.rootView
        val visibleShowcase by showcaseVisible.collectAsStateWithLifecycle()
        val currentToolTipTarget by tooltipTargetView.collectAsStateWithLifecycle()

        LaunchedEffect(currentToolTipTarget) {
            if (currentToolTipTarget?.target == targetKey){
                val campaign = campaigns.value.firstOrNull { it.campaignType == "TTP" && it.details is TooltipsDetails }

                repository.trackTooltipsActions(accessToken, TrackActionTooltips(
                    campaign_id = campaign?.id,
                    user_id = userId,
                    event_type = "IMP",
                    tooltip_id = currentToolTipTarget!!.id

                ))
            }
        }

        Box(modifier = targetModifier) {
            TooltipPopup(
                modifier = Modifier
                    .padding(start = 8.dp),
                requesterView = { modifier ->
                    requesterView(modifier.onGloballyPositioned { coordinates ->
                        _viewsCoordinates.value = _viewsCoordinates.value.toMutableMap().apply {
                            put(targetKey, coordinates)
                        }
                        position = calculateTooltipPopupPosition(view, coordinates)
                    })
                },
                backgroundColor = Color.White,
                position = position,
                isShowTooltip = visibleShowcase && currentToolTipTarget?.target == targetKey,
                onDismissRequest = {
                    coroutineScope.launch {
                        _tooltipTargetView.emit(null)
                        _showcaseVisible.emit(false)
                    }
                },
                tooltip = if (currentToolTipTarget?.target == targetKey) currentToolTipTarget else null,
                tooltipContent = {
                    if (currentToolTipTarget?.target == targetKey){
                        TooltipContent(tooltip = currentToolTipTarget!!, exitUnit = {
                            coroutineScope.launch {
                                _tooltipTargetView.emit(null)
                                _showcaseVisible.emit(false)
                            }
                        }, onClick = {
                            coroutineScope.launch{
                                if (!currentToolTipTarget!!.link.isNullOrEmpty()){
                                    if (!isValidUrl(currentToolTipTarget!!.link)) {
                                        currentToolTipTarget!!.link?.let { navigateToScreen(it) }
                                    } else {
                                        currentToolTipTarget!!.link?.let { openUrl(it) }
                                    }

                                    val campaign = campaigns.value.firstOrNull { it.campaignType == "TTP" && it.details is TooltipsDetails }

                                    repository.trackTooltipsActions(accessToken, TrackActionTooltips(
                                        campaign_id = campaign?.id,
                                        user_id = userId,
                                        event_type = "CLK",
                                        tooltip_id = currentToolTipTarget!!.id

                                    ))
                                }
                            }
                        })
                    }
                }
            )
        }

    }


    @Composable
    fun ShowCaseScreen() {
        val coordinates by viewsCoordinates.collectAsStateWithLifecycle()
        val visibleShowcase by showcaseVisible.collectAsStateWithLifecycle()
        val currentToolTipTarget by tooltipTargetView.collectAsStateWithLifecycle()

        coordinates[currentToolTipTarget?.target]?.let {
            ShowcaseView(
                visible = visibleShowcase,
                targetCoordinates = it,
                highlight = ShowcaseHighlight.Circular()
            )
        }
    }

    private fun showCaseInformation() {
        coroutineScope.launch {
            combine(
                campaigns,
                viewsCoordinates
            ) { campaignList, coordinates -> campaignList to coordinates }.collectLatest { (campaignList, coordinates) ->
                val campaign = campaignList.firstOrNull { it.campaignType == "TTP" && it.details is TooltipsDetails }
                val tooltipsDetails = campaign?.details as? TooltipsDetails
                if (tooltipsDetails != null) {
                    for (tooltip in tooltipsDetails.tooltips?.sortedBy { it.order } ?: emptyList()) {
                        if (tooltip.target != null && !_tooltipViewed.value.contains(tooltip.target)) {

                            if (coordinates.contains(tooltip.target)) {
                                while (_tooltipTargetView.value != null) {
                                    delay(500L)
                                }
                                _tooltipTargetView.emit(tooltip)
                                _showcaseVisible.emit(true)
                                _tooltipViewed.emit(tooltipViewed.value.toMutableList().apply { add(tooltip.target) })

                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun Stories() {
        val campaignsData = campaigns.collectAsStateWithLifecycle()
        val campaign = campaignsData.value.firstOrNull { it.campaignType == "STR" }
        val storiesDetails = (campaign?.details as? List<*>)?.filterIsInstance<StoryGroup>()

        if (!storiesDetails.isNullOrEmpty()) {
            StoryAppMain(apiStoryGroups = storiesDetails, sendEvent = {
                coroutineScope.launch {
                    repository.trackStoriesActions(
                        accessToken, TrackActionStories(
                            campaign_id = campaign.id,
                            user_id = userId,
                            story_slide = it.first.id,
                            event_type = it.second
                        )
                    )
                }
            }
            )

        }

    }

    @Composable
    fun Reels(modifier: Modifier = Modifier) {
        val campaignsData = campaigns.collectAsStateWithLifecycle()
        val campaign =
            campaignsData.value.firstOrNull { it.campaignType == "REL" && it.details is ReelsDetails }
        val reelsDetails = campaign?.details as? ReelsDetails
        val selectedReelIndex by selectedReelIndex.collectAsStateWithLifecycle()
        val visibility by reelFullScreenVisible.collectAsStateWithLifecycle()

        if (reelsDetails?.reels != null && reelsDetails.reels.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {

                ReelsRow(
                    modifier = modifier,
                    reels = reelsDetails.reels,
                    onReelClick = { index ->
                        coroutineScope.launch {
                            _selectedReelIndex.emit(index)
                            _reelFullScreenVisible.emit(true)
                        }
                    }
                )

                if (visibility) {
                    ReelFullScreen(
                        campaignId = campaign.id,
                        reelsDetails = reelsDetails,
                        selectedReelIndex = selectedReelIndex
                    ) {
                        coroutineScope.launch {
                            _selectedReelIndex.emit(0)
                            _reelFullScreenVisible.emit(false)
                        }
                    }
                }
            }
        }

    }

    @Composable
    private fun ReelFullScreen(
        campaignId: String?,
        reelsDetails: ReelsDetails,
        selectedReelIndex: Int,
        onDismiss: () -> Unit
    ) {
        if (!reelsDetails.reels.isNullOrEmpty()) {

            var likedReels by remember {
                mutableStateOf(
                    getLikedReels(
                        context.getSharedPreferences(
                            "AppStory",
                            Context.MODE_PRIVATE
                        )
                    )
                )
            }

            Dialog(
                onDismissRequest = onDismiss,
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false
                )
            ) {

                BackHandler {
                    coroutineScope.launch {
                        _selectedReelIndex.emit(0)
                        _reelFullScreenVisible.emit(false)
                    }
                }

                FullScreenVideoScreen(
                    reels = reelsDetails.reels,
                    likedReels = likedReels,
                    startIndex = selectedReelIndex,
                    sendLikesStatus = {
                        coroutineScope.launch {


                            if (it.second == "like") {
                                val list = ArrayList(likedReels)
                                list.add(it.first.id)
                                likedReels = list.distinct()
                                saveLikedReels(
                                    idList = list.distinct(),
                                    sharedPreferences = context.getSharedPreferences(
                                        "AppStory",
                                        Context.MODE_PRIVATE
                                    )
                                )
                            } else {
                                val list = ArrayList(likedReels)
                                list.remove(it.first.id)
                                likedReels = list.distinct()
                                saveLikedReels(
                                    idList = list.distinct(),
                                    sharedPreferences = context.getSharedPreferences(
                                        "AppStory",
                                        Context.MODE_PRIVATE
                                    )
                                )
                            }

                            repository.sendReelLikeStatus(
                                accessToken = accessToken,
                                actions = ReelStatusRequest(
                                    user_id = userId,
                                    action = it.second,
                                    reel = it.first.id
                                )
                            )
                        }
                    },
                    sendEvents = {
                        if (it.second == "IMP") {
                            if (!_impressions.value.contains(it.first.id)) {
                                coroutineScope.launch {
                                    val impressions = ArrayList(impressions.value)
                                    impressions.add(it.first.id)
                                    _impressions.emit(impressions)
                                    repository.trackReelActions(
                                        accessToken = accessToken,
                                        actions = ReelActionRequest(
                                            user_id = userId,
                                            reel_id = it.first.id,
                                            event_type = it.second,
                                            campaign_id = campaignId
                                        )
                                    )
                                }
                            }
                        } else {
                            coroutineScope.launch {
                                repository.trackReelActions(
                                    accessToken = accessToken,
                                    actions = ReelActionRequest(
                                        user_id = userId,
                                        reel_id = it.first.id,
                                        event_type = it.second,
                                        campaign_id = campaignId
                                    )
                                )
                            }
                        }

                    },
                    onBack = {
                        coroutineScope.launch {
                            _selectedReelIndex.emit(0)
                            _reelFullScreenVisible.emit(false)
                        }
                    }
                )
            }
        }
    }


    @Composable
    fun PinnedBanner(
        modifier: Modifier = Modifier,
        contentScale: ContentScale = ContentScale.FillWidth,
        staticHeight: Dp = 200.dp,
        placeHolder: Drawable?,
        position: String?
    ) {
        val campaignsData = campaigns.collectAsStateWithLifecycle()
        val disabledCampaigns = disabledCampaigns.collectAsStateWithLifecycle()

        val campaign = position?.let { pos -> campaignsData.value.filter { it.position == pos } }
            ?.firstOrNull { it.campaignType == "BAN" }
            ?: campaignsData.value.firstOrNull { it.campaignType == "BAN" }

        Log.i("BannedPinner", campaign.toString())
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
                        clickEvent(url = bannerDetails.link ?: "", campaignId = it)
                    }
                },
                imageUrl = bannerUrl ?: "",
                lottieUrl = bannerDetails.lottie_data,
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
        contentScale: ContentScale = ContentScale.FillWidth,
        staticHeight: Dp = 200.dp,
        placeHolder: Drawable?,
        position: String?
    ) {
        val campaignsData = campaigns.collectAsStateWithLifecycle()
        val campaign =
            campaignsData.value.filter { it.campaignType == "WID" && it.details is WidgetDetails }
                .firstOrNull { it.position == position }
        val widgetDetails = campaign?.details as? WidgetDetails

        if (widgetDetails != null) {
            Log.i("WidgetPics", widgetDetails.widgetImages.toString())

            if (widgetDetails.type == "full") {


                FullWidget(
                    modifier = modifier,
                    staticHeight = staticHeight,
                    placeHolder = placeHolder,
                    contentScale = contentScale,
                    position = position
                )

            } else if (widgetDetails.type == "half") {
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
        contentScale: ContentScale = ContentScale.FillWidth,
        staticHeight: Dp = 200.dp,
        placeHolder: Drawable?,
        position: String?
    ) {
        val campaignsData = campaigns.collectAsStateWithLifecycle()
        val disabledCampaigns = disabledCampaigns.collectAsStateWithLifecycle()
        val campaign = campaignsData.value
            .filter { it.campaignType == "WID" && it.details is WidgetDetails && it.position == position }
            .firstOrNull { (it.details as WidgetDetails).type == "full" }

        val widgetDetails = (campaign?.details as? WidgetDetails)

        // Track if the widget is visible in the viewport
        var isVisible by remember { mutableStateOf(false) }

//        Log.i("WidgetDetails", campaignsData.value.filter{
//            it.campaignType == "WID" && it.details is WidgetDetails
//        }.toString())

        if (widgetDetails?.widgetImages != null && widgetDetails.widgetImages.isNotEmpty() && campaign.id != null && !disabledCampaigns.value.contains(
                campaign.id
            ) && widgetDetails.type == "full"
        ) {
            val pagerState = rememberPagerState(pageCount = {
                widgetDetails.widgetImages.count()
            })
            val heightInDp: Dp? = widgetDetails.height?.dp

            LaunchedEffect(pagerState.currentPage, isVisible) {
                if (isVisible) {
                    campaign?.id?.let {
                        trackCampaignActions(
                            it,
                            "IMP",
                            widgetDetails.widgetImages[pagerState.currentPage].id
                        )
                    }
                }
            }

            AutoSlidingCarousel(
                modifier = modifier.onGloballyPositioned { layoutCoordinates ->
                    val visibilityRect = layoutCoordinates.boundsInWindow()
                    // Consider widget visible if at least 50% of it is in the viewport
                    val parentHeight = layoutCoordinates.parentLayoutCoordinates?.size?.height ?: 0
                    val widgetHeight = layoutCoordinates.size.height
                    val isAtLeastHalfVisible = visibilityRect.top < parentHeight &&
                            visibilityRect.bottom > 0 &&
                            (visibilityRect.height >= widgetHeight * 0.5f)

                    isVisible = isAtLeastHalfVisible
                },
                pagerState = pagerState,
                itemsCount = widgetDetails.widgetImages.count(),
                itemContent = { index ->
                    widgetDetails.widgetImages[index].link?.let {
                        CarousalImage(
                            modifier = Modifier.clickable {
                                clickEvent(
                                    url = it,
                                    campaignId = campaign.id,
                                    widgetImageId = widgetDetails.widgetImages[index].id
                                )
                            },
                            contentScale = contentScale,
                            imageUrl = widgetDetails.widgetImages[index].image ?: "",
                            placeHolder = placeHolder,
                            height = heightInDp ?: staticHeight,
                        )
                    }
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

        val campaign = campaignsData.value
            .filter { it.campaignType == "WID" && it.details is WidgetDetails && it.position == position }
            .firstOrNull { (it.details as WidgetDetails).type == "half" }


        val widgetDetails = (campaign?.details as? WidgetDetails)

        // Track if the widget is visible in the viewport
        var isVisible by remember { mutableStateOf(false) }

        if (widgetDetails != null && campaign.id != null &&
            !disabledCampaigns.value.contains(campaign.id) && widgetDetails.widgetImages != null && widgetDetails.type == "half"
        ) {

            val heightInDp: Dp? = widgetDetails.height?.dp
            val widgetImagesPairs = widgetDetails.widgetImages.turnToPair()
            val pagerState = rememberPagerState(pageCount = {
                widgetImagesPairs.count()
            })

            // Only trigger impression tracking when both visible and page changes
            LaunchedEffect(pagerState.currentPage, isVisible) {
                if (isVisible) {
                    campaign?.id?.let {
                        // Track impression for left image
                        trackCampaignActions(
                            it,
                            "IMP",
                            widgetImagesPairs[pagerState.currentPage].first.id
                        )

                        // Track impression for right image
                        trackCampaignActions(
                            it,
                            "IMP",
                            widgetImagesPairs[pagerState.currentPage].second.id
                        )
                    }
                }
            }

            DoubleWidgets(
                modifier = modifier.onGloballyPositioned { layoutCoordinates ->
                    val visibilityRect = layoutCoordinates.boundsInWindow()
                    // Consider widget visible if at least 50% of it is in the viewport
                    val parentHeight = layoutCoordinates.parentLayoutCoordinates?.size?.height ?: 0
                    val widgetHeight = layoutCoordinates.size.height
                    val isAtLeastHalfVisible = visibilityRect.top < parentHeight &&
                            visibilityRect.bottom > 0 &&
                            (visibilityRect.height >= widgetHeight * 0.5f)

                    isVisible = isAtLeastHalfVisible
                },
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
                        if (leftImage.image != null) {
                            ImageCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (leftImage.link != null) {
                                            clickEvent(
                                                url = leftImage.link,
                                                campaignId = campaign.id,
                                                widgetImageId = leftImage.id
                                            )
                                        }

                                    },
                                imageUrl = leftImage.image,
                                height = heightInDp ?: staticHeight
                            )

                        }

                        if (rightImage.image != null) {
                            ImageCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (rightImage.link != null) {
                                            clickEvent(
                                                url = rightImage.link,
                                                campaignId = campaign.id,
                                                widgetImageId = rightImage.id
                                            )
                                        }
                                    },
                                imageUrl = rightImage.image,
                                height = heightInDp ?: staticHeight
                            )
                        }

                    }
                }
            )
        }
    }

    private fun clickEvent(url: String?, campaignId: String, widgetImageId: String? = null) {

        if(!url.isNullOrEmpty()){
            if (!isValidUrl(url)) {
                navigateToScreen(url)
            } else {
                openUrl(url)
            }

            trackCampaignActions(campaignId, "CLK", widgetImageId)
        }
    }


    private fun List<WidgetImage>.turnToPair(): List<Pair<WidgetImage, WidgetImage>> {
        if (this.isEmpty()) {
            return emptyList()
        }
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
            if (eventType != "CLK") {
                if (widgetImageId != null && !impressions.value.contains(widgetImageId)) {
                    val impressions = ArrayList(impressions.value)
                    impressions.add(widgetImageId)
                    _impressions.emit(impressions)
                    repository.trackActions(
                        accessToken,
                        TrackAction(campId, userId, eventType, widgetImageId)
                    )
                } else if (!impressions.value.contains(campId)) {
                    val impressions = ArrayList(impressions.value)
                    impressions.add(campId)
                    _impressions.emit(impressions)
                    repository.trackActions(
                        accessToken,
                        TrackAction(campId, userId, eventType, null)
                    )
                }
            } else {
                repository.trackActions(
                    accessToken,
                    TrackAction(campId, userId, eventType, widgetImageId)
                )
            }
        }
    }


    private fun isValidUrl(url: String?): Boolean {
        return !url.isNullOrEmpty() && Patterns.WEB_URL.matcher(url).matches()
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (_: Exception) {

        }

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

        private const val KEY_FLOATER = "KEY_FLOATER"

    }


}
