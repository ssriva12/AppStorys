package com.appversal.appstorys.ui

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.foundation.background
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import com.appversal.appstorys.api.Reel
import org.json.JSONArray


@Composable
internal fun ReelsRow(modifier : Modifier, reels: List<Reel>, onReelClick: (Int) -> Unit) {

        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(reels.size) { index ->
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .padding(end = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onReelClick(index) }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(reels[index].thumbnail),
                        contentDescription = "Thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

}

@Composable
internal fun FullScreenVideoScreen(reels: List<Reel>, likedReels: List<String>, startIndex: Int, onBack: () -> Unit, sendLikesStatus: (Pair<Reel, String>) -> Unit, sendEvents: (Pair<Reel, String>) -> Unit) {
    val screenHeight = getScreenHeight().dp
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { reels.size })
    val context = LocalContext.current

    // Track likes state for each reel
    val likesState = remember {
        reels.map { reel ->
            mutableStateOf(reel.likes)
        }
    }



    val players = remember {
        reels.map { reel ->

            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(reel.video)))
                repeatMode = Player.REPEAT_MODE_ONE
                prepare()
            }
        }
    }

    // Handle play/pause when the page changes
    LaunchedEffect(pagerState.currentPage) {
        players.forEach { it.playWhenReady = false }
        players[pagerState.currentPage].playWhenReady = true
        sendEvents(Pair(reels[pagerState.currentPage], "IMP"))
    }

    // Clean up players when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            players.forEach { it.release() }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight)
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Video Player
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            useController = false // Hide controls
                            player = players[page]

                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )

                // UI Controls overlay
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    // Like/Share buttons row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(20f))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Like Button
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    //isLikedState[page].value = !isLikedState[page].value
                                    if (!likedReels.contains(reels[page].id)) {
                                        likesState[page].value += 1
                                    } else {
                                        likesState[page].value -= 1
                                    }

                                    sendLikesStatus(Pair(reels[page],if ( !likedReels.contains(reels[page].id)) "like" else "unlike"))
                                }
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Favorite,
                                    contentDescription = "Like",
                                    tint = if (likedReels.contains(reels[page].id)) Color.Green else Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = likesState[page].value.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Share Button
                            if (reels[page].link.isNotEmpty()) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable {
                                        val sendIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_TEXT, reels[page].link)
                                            type = "text/plain"
                                        }
                                        val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                        context.startActivity(shareIntent)
                                    }
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Share",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Description and Button Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0f, 0f, 0f, 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            // Description Text
                            if (reels[page].descriptionText.isNotEmpty()) {
                                Text(
                                    text = reels[page].descriptionText,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Button
                            if (reels[page].link.isNotEmpty() && reels[page].buttonText.isNotEmpty()) {
                                Button(
                                    onClick = {
                                        sendEvents(Pair(reels[page], "CLK"))
                                        try {
                                            val uri = Uri.parse(reels[page].link)
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Show error toast or snackbar
                                            android.widget.Toast.makeText(
                                                context,
                                                "Could not open link",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                ) {
                                    Text(
                                        text = reels[page].buttonText,
                                        color = Color.Black,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(screenHeight.times(0.08f)))
                        }
                    }
                }
            }
        }

        // Back Button
        Button(
            onClick = { onBack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text("Back")
        }
    }
}


internal fun saveLikedReels(idList: List<String>, sharedPreferences: SharedPreferences) {
    val jsonArray = JSONArray(idList)
    sharedPreferences.edit().putString("LIKED_REELS", jsonArray.toString()).apply()
}

internal fun getLikedReels(sharedPreferences: SharedPreferences): List<String> {
    val jsonString = sharedPreferences.getString("LIKED_REELS", "[]") ?: "[]"
    val jsonArray = JSONArray(jsonString)
    return List(jsonArray.length()) { jsonArray.getString(it) }
}

@Composable
fun getScreenHeight(): Int {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp
}
