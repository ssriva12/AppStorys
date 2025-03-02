package com.appversal.appstorys.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.appversal.appstorys.R
import com.appversal.appstorys.api.StoryGroup
import com.appversal.appstorys.api.StorySlide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import kotlin.coroutines.cancellation.CancellationException

@Composable
internal fun StoryCircles(storyGroups: List<StoryGroup>, onStoryClick: (StoryGroup) -> Unit, viewedStories: List<String>) {

    // Sort story groups by order
    val sortedStoryGroups = remember(storyGroups, viewedStories) {
        storyGroups.sortedWith(
            compareByDescending<StoryGroup> { it.id !in viewedStories }
                .thenBy { it.order }
        )
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sortedStoryGroups.size) { index ->
            val storyGroup = sortedStoryGroups[index]
            if (storyGroup.thumbnail != null){
                StoryItem(
                    isStoryGroupViewed = viewedStories.contains(storyGroup.id),
                    imageUrl = storyGroup.thumbnail,
                    username = storyGroup.name ?: "",
                    ringColor = Color(android.graphics.Color.parseColor(storyGroup.ringColor)),
                    nameColor = Color(android.graphics.Color.parseColor(storyGroup.nameColor)),
                    onClick = { onStoryClick(storyGroup) }
                )
            }
        }
    }
}

@Composable
internal fun StoryItem(
    isStoryGroupViewed: Boolean,
    imageUrl: String,
    username: String,
    ringColor: Color,
    nameColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(70.dp)
        ) {
            Canvas(
                modifier = Modifier.size(80.dp)
            ) {
                drawCircle(
                    color = if (isStoryGroupViewed) Color.Gray else ringColor,
                    style = Stroke(width = 5f),
                    radius = size.minDimension / 2
                )
            }

            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(65.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(modifier = Modifier.width(60.dp).align(Alignment.CenterHorizontally), text = username, maxLines = 2, fontSize = 12.sp, color = nameColor, textAlign = TextAlign.Center, lineHeight = 15.sp)
    }
}

@Composable
internal fun StoryScreen(
    storyGroup: StoryGroup,
    onDismiss: () -> Unit,
    slides: List<StorySlide>,
    onStoryGroupEnd: () -> Unit,
    sendEvent: (Pair<StorySlide, String>) -> Unit
) {

    var currentSlideIndex by remember { mutableIntStateOf(0) }
    val currentSlide = slides.get(currentSlideIndex)
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    var isHolding by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    // Progress state
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(targetValue = progress)


    // Track completed slides
    val completedSlides = remember { mutableStateListOf<Int>() }

    // Timer for image stories (5 seconds)
    val isImage = currentSlide.image != null
    val storyDuration = if (isImage) 5000 else 0 // 5 seconds for images

    // Create and remember the ExoPlayer instance
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = true
        }
    }

    fun handleSlideCompletion() {
        if (!completedSlides.contains(currentSlideIndex)) {
            completedSlides.add(currentSlideIndex)
        }

        // Small delay before moving to the next slide
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            if (currentSlideIndex < slides.lastIndex) {
                currentSlideIndex++
            } else {
                onStoryGroupEnd()
                currentSlideIndex = 0
                completedSlides.clear()
            }
        }
    }
    // Clean up player when dialog is dismissed
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(currentSlideIndex, isImage) {
        progress = 0f

        sendEvent(Pair(currentSlide, "IMP"))
        if (isImage) {
            var accumulatedTime = 0L
            var startTime = System.currentTimeMillis()

            while (progress < 1f) {
                if (!isHolding) {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    progress = ((accumulatedTime + elapsedTime).toFloat() / storyDuration).coerceIn(0f, 1f)
                } else {
                    accumulatedTime += System.currentTimeMillis() - startTime
                    while (isHolding) delay(16)
                    startTime = System.currentTimeMillis()
                }
                delay(16)
            }
            handleSlideCompletion()
        }
    }


    // Load video when slide changes
    LaunchedEffect(currentSlide) {
        if (!isImage) {
            currentSlide.video?.let { videoUrl ->
                exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl.toUri()))
                exoPlayer.prepare()

                // Track video progress and handle completion
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_ENDED) {
                            handleSlideCompletion()
                        }
                    }
                })
            }
        }
    }

    // Track video progress
    LaunchedEffect(Unit) {
        while (true) {
            if (exoPlayer.duration > 0) {
                progress =
                    (exoPlayer.currentPosition.toFloat() / exoPlayer.duration).coerceIn(0f, 1f)
            }
            delay(16) // Update roughly every frame
        }
    }

    // Function to share content
    val shareContent = {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this story: ${currentSlide.link}")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            val job = coroutineScope.launch {
                                delay(500)
                                isHolding = true
                                if (!isImage) exoPlayer.pause()
                            }
                            try {
                                awaitRelease()
                            } finally {
                                if (!isImage) exoPlayer.play()
                                job.cancel()
                                coroutineScope.launch {
                                    delay(200)
                                    isHolding = false
                                }
                            }

                        },
                        onTap = { tapOffset ->


                            if (!isHolding){
                                // Determine if tap was on left or right side
                                val screenWidth = this.size.width
                                if (tapOffset.x < screenWidth / 2) {
                                    // Left side tap - go to previous slide
                                    if (currentSlideIndex > 0) {
                                        if (completedSlides.contains(currentSlideIndex)) {
                                            completedSlides.remove(currentSlideIndex)
                                        }
                                        currentSlideIndex--
                                    }
                                } else {
                                    // Right side tap - go to next slide
                                    if (currentSlideIndex < slides.lastIndex) {
                                        // Mark current slide as completed when manually skipping
                                        if (!completedSlides.contains(currentSlideIndex)) {
                                            completedSlides.add(currentSlideIndex)
                                        }
                                        currentSlideIndex++
                                    } else {
                                        //onDismiss()
                                        onStoryGroupEnd()
                                        currentSlideIndex = 0
                                        completedSlides.clear()

                                    }
                                }
                            }

                        }
                    )
                }
        ) {
            // Progress indicators - one for each slide
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                slides.forEachIndexed { index, _ ->
                    val progressValue = when {
                        index == currentSlideIndex -> animatedProgress
                        index < currentSlideIndex || completedSlides.contains(index) -> 1f
                        else -> 0f
                    }

                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp),
                        color = Color.White,
                        trackColor = Color.Gray.copy(alpha = 0.5f),
                        drawStopIndicator = {}
                    )
                }
            }

            // Profile section (Left side)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Profile image
                Image(
                    painter = rememberAsyncImagePainter(storyGroup.thumbnail),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Username/Story group name
                storyGroup.name?.let {
                    Text(
                        text = it,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            // Top controls row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isImage){
                    IconButton(onClick = {
                        isMuted = !isMuted
                        if (isMuted){
                            exoPlayer.volume = 0f
                        }else{
                            exoPlayer.volume = 1f
                        }
                    }) {

                        Icon(
                            painter = if (isMuted) painterResource(R.drawable.mute) else painterResource(R.drawable.volume),
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                            tint = Color.White
                        )
                    }
                }


                // Share button
                if (currentSlide.link?.isNotEmpty() == true && currentSlide.buttonText?.isNotEmpty() == true){
                    IconButton(onClick = shareContent) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Close button
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }


            // Content
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Image content
                if (currentSlide.image != null) {
                    Image(
                        painter = rememberAsyncImagePainter(currentSlide.image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Video content
                if (currentSlide.video != null) {
                    AndroidView(
                        factory = { ctx ->
                            StyledPlayerView(ctx).apply {
                                player = exoPlayer
                                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                useController = false
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Link button if available
                if (currentSlide.link?.isNotEmpty() == true && currentSlide.buttonText?.isNotEmpty() == true) {
                    Button(
                        onClick = {
                            uriHandler.openUri(currentSlide.link)
                            sendEvent(Pair(currentSlide, "CLK"))
                                  },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                                colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                                )

                    ) {
                        Text(text = currentSlide.buttonText, color = Color.Black)
                    }
                }

                // Invisible touch areas for left/right navigation (for visual debugging)
                /* Uncomment for debugging touch areas
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color.Red.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color.Green.copy(alpha = 0.2f))
                    )
                }
                */
            }
        }
    }
}

@Composable
internal fun StoriesApp(storyGroups: List<StoryGroup>, sendEvent: (Pair<StorySlide, String>) -> Unit, viewedStories: List<String>, storyViewed: (String) -> Unit) {
    var selectedStoryGroup by remember { mutableStateOf<StoryGroup?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        StoryCircles(
            viewedStories = viewedStories,
            storyGroups = storyGroups,
            onStoryClick = { storyGroup ->
                selectedStoryGroup = storyGroup
                selectedStoryGroup?.id?.let{
                    storyViewed(it)
                }
            }
        )

        selectedStoryGroup?.let { storyGroup ->
            if (!storyGroup.slides.isNullOrEmpty()){
                key(storyGroup) {
                    StoryScreen(
                        storyGroup = storyGroup,
                        slides = storyGroup.slides,
                        onDismiss = { selectedStoryGroup = null },
                        onStoryGroupEnd = {
                            val currentIndex = storyGroups.indexOf(storyGroup)
                            if (currentIndex < storyGroups.lastIndex) {
                                selectedStoryGroup = storyGroups[currentIndex + 1]
                                selectedStoryGroup?.id?.let{
                                    storyViewed(it)
                                }
                            } else {
                                selectedStoryGroup = null
                            }
                        },
                        sendEvent = sendEvent
                    )
                }
            }
        }
    }
}

// Example usage with your data
@Composable
internal fun StoryAppMain(apiStoryGroups: List<StoryGroup>, sendEvent: (Pair<StorySlide, String>) -> Unit) {

    val context = LocalContext.current
    var viewedStories by remember { mutableStateOf(getViewedStories(context.getSharedPreferences("AppStory", Context.MODE_PRIVATE))) }
    var storyGroups by remember { mutableStateOf(apiStoryGroups.sortedWith(
        compareByDescending<StoryGroup> { it.id !in viewedStories }
            .thenBy { it.order })) }

    LaunchedEffect(viewedStories) {
        storyGroups = storyGroups.sortedWith(
            compareByDescending<StoryGroup> { it.id !in viewedStories }
                .thenBy { it.order }
        )
    }

    StoriesApp(storyGroups = storyGroups, sendEvent = sendEvent, viewedStories = viewedStories, storyViewed =  {
        if (!viewedStories.contains(it)){
            val list = ArrayList(viewedStories)
            list.add(it)
            viewedStories = list
            saveViewedStories(idList = list, sharedPreferences = context.getSharedPreferences("AppStory", Context.MODE_PRIVATE))
        }
    })
}

internal fun saveViewedStories(idList: List<String>, sharedPreferences: SharedPreferences) {
    val jsonArray = JSONArray(idList)
    sharedPreferences.edit().putString("VIEWED_STORIES", jsonArray.toString()).apply()
}

internal fun getViewedStories(sharedPreferences: SharedPreferences): List<String> {
    val jsonString = sharedPreferences.getString("VIEWED_STORIES", "[]") ?: "[]"
    val jsonArray = JSONArray(jsonString)
    return List(jsonArray.length()) { jsonArray.getString(it) }
}

/*
*
* val storyGroups = listOf(
        StoryGroup(
            id = "6b3c7e5c-3a5b-49cd-9b25-25d6c81b7b2a",
            name = "How To",
            thumbnail = "https://appstorysmediabucket.s3.amazonaws.com/story_groups/Enjoy_Zero_Convenience_Fee_BOOK_NOW_10.png",
            ringColor = "#FD5F03",
            nameColor = "#000000",
            order = 1,
            slides = listOf(
                StorySlide(
                    id = "8a42c55f-e302-41e9-be82-42c49adf1bdd",
                    parent = "6b3c7e5c-3a5b-49cd-9b25-25d6c81b7b2a",
                    image = null,
                    video = "https://appstorysmediabucket.s3.amazonaws.com/story_slides/Untitled_design_14.mp4",
                    link = "",
                    buttonText = "",
                    order = 1
                )
            )
        ),
        StoryGroup(
            id = "b7d85bf2-09c6-4a6f-880c-814f0889f357",
            name = "Success",
            thumbnail = "https://appstorysmediabucket.s3.amazonaws.com/story_groups/Enjoy_Zero_Convenience_Fee_BOOK_NOW_13.png",
            ringColor = "#FD5F03",
            nameColor = "#000000",
            order = 3,
            slides = listOf(
                StorySlide(
                    id = "755cc594-29af-4c75-ab8b-24dc4935942b",
                    parent = "b7d85bf2-09c6-4a6f-880c-814f0889f357",
                    image = null,
                    video = "https://appstorysmediabucket.s3.amazonaws.com/story_slides/47_3.mp4",
                    link = "https://appstorys.com/",
                    buttonText = "AppStorys",
                    order = 2
                ),
                StorySlide(
                    id = "b6fb0fe8-e151-4b7e-be76-3abfbe0f59fb",
                    parent = "b7d85bf2-09c6-4a6f-880c-814f0889f357",
                    image = null,
                    video = "https://appstorysmediabucket.s3.amazonaws.com/story_slides/29_2.mp4",
                    link = "",
                    buttonText = "",
                    order = 2
                )
            )
        )
    )
    * */